package reflection;

import annotation.postgresql.Id;
import annotation.postgresql.Model;
import org.reflections.Reflections;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
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
            assert modelAnno != null;
            String tableName = modelAnno.table();
            StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableName + " (");

            Field[] fields = clazz.getDeclaredFields();
            List<String> columns = new ArrayList<>();
            String primaryKey = null;

            for (Field field : fields) {
                String name = field.getName();
                String type = mapJavaToSQL(field.getType());

                if (field.isAnnotationPresent(Id.class)) {
                    primaryKey = name;
                }

                columns.add(name + " " + type);
            }

            sql.append(String.join(", ", columns));
            if (primaryKey != null) {
                sql.append(", PRIMARY KEY(").append(primaryKey).append(")");
            }
            sql.append(");");
            Statement stmt = conn.createStatement();
            stmt.execute(sql.toString());
        }
    }

    private String mapJavaToSQL(Class<?> javaType) {
        if (javaType == int.class || javaType == Integer.class) return "INTEGER";
        if (javaType == long.class || javaType == Long.class) return "BIGINT";
        if (javaType == String.class) return "TEXT";
        if (javaType == boolean.class || javaType == Boolean.class) return "BOOLEAN";
        return "TEXT";
    }
}
