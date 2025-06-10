package reflection;

import annotation.postgresql.GeneratedValue;
import annotation.postgresql.Id;
import annotation.postgresql.Model;
import enm.GenerationType;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModelTableCreator {
    private final Connection conn;

    public ModelTableCreator(Connection conn) {
        this.conn = conn;
    }

    public void createTables(String basePackage) throws Exception {
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> modelClasses = reflections.getTypesAnnotatedWith(Model.class);

        for (Class<?> clazz : modelClasses) {
            Model modelAnno = clazz.getAnnotation(Model.class);
            if (modelAnno == null) throw new IllegalStateException("modelAnno is null");

            String tableName = modelAnno.table();
            DatabaseMetaData metaData = conn.getMetaData();

            ResultSet tables = metaData.getTables(null, null, tableName, null);
            boolean tableExists = tables.next();

            if (!tableExists) {
                StringBuilder sql = new StringBuilder("CREATE TABLE " + tableName + " (");
                Field[] fields = clazz.getDeclaredFields();
                List<String> columns = new ArrayList<>();
                String primaryKey = null;
                boolean idAlreadyHandled = false;

                for (Field field : fields) {
                    String name = camelToSnake(field.getName());
                    Class<?> type = field.getType();
                    if (field.isAnnotationPresent(Id.class)) {
                        primaryKey = name;
                        GeneratedValue gv = field.getAnnotation(GeneratedValue.class);
                        if (gv != null && gv.strategy() == GenerationType.IDENTITY) {
                            columns.add(name + " BIGSERIAL PRIMARY KEY");
                            idAlreadyHandled = true;
                            continue;
                        }
                    }
                    String sqlType = mapJavaToSQL(type);
                    columns.add(name + " " + sqlType);
                }

                sql.append(String.join(", ", columns));
                if (primaryKey != null && !idAlreadyHandled) {
                    sql.append(", PRIMARY KEY(").append(primaryKey).append(")");
                }

                sql.append(");");
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(sql.toString());
                }
            } else {
                ResultSet columns = metaData.getColumns(null, null, tableName, null);
                Set<String> existingColumns = new HashSet<>();
                while (columns.next()) {
                    existingColumns.add(columns.getString("COLUMN_NAME"));
                }

                for (Field field : clazz.getDeclaredFields()) {
                    String columnName = camelToSnake(field.getName());
                    if (!existingColumns.contains(columnName)) {
                        String sqlType = mapJavaToSQL(field.getType());
                        String alterSql = "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + sqlType;
                        try (Statement stmt = conn.createStatement()) {
                            stmt.execute(alterSql);
                        }
                    }
                }
            }
        }
    }


    private String mapJavaToSQL(Class<?> javaType) {
        if (javaType == int.class || javaType == Integer.class) return "INTEGER";
        if (javaType == long.class || javaType == Long.class) return "BIGINT";
        if (javaType == String.class) return "TEXT";
        if (javaType == boolean.class || javaType == Boolean.class) return "BOOLEAN";
        if (javaType == LocalDate.class) return "DATE";
        if (javaType == LocalDateTime.class) return "TIMESTAMP";
        return "TEXT";
    }

    public String camelToSnake(String input) {
        return input.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }

}



