package com.yvr;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

public class Program {

    public static void main(String[] args) throws Exception {

        // To Draw Inspiration:
        // https://github.com/galperin/Solutions-for-exercises-from-Java-SE-8-for-the-Really-Impatient-by-Horstmann
        System.out.println("Doing a refactoring");
        /*
        LambdaPlay lambdaPlay = new LambdaPlay();
        lambdaPlay.ch06_01_AtomicValues();
        */

        /*
        String fos = "AAAA";
        String fos2 = System.getProperty("marketplace");
        String fos3 = fos2 + fos;
        System.out.println(fos3);
        */

        /*
        boolean a = false;
        a = a && foo();
        */

        /*
        String expectedContent = "Tu membresía de Amazon Prime terminará el [0-9]+\\s[A-Za-z]+\\s20[0-9]{2}. El [0-9]+\\s[A-Za-z]+\\s20[0-9]{2}, se te cobrará \\$899\\.00 por un año de Amazon Prime\\.";
        String actualContent = "Tu membresía de Amazon Prime terminará el 28 octubre 2018. El 28 octubre 2018, se te cobrará $899.00 por un año de Amazon Prime.";

        System.out.println(actualContent.matches(expectedContent));
        */


        /*
        class AnonymousClass {
            final String field1;
            final String field2;

            AnonymousClass(String field1, String field2) {
                this.field1 = field1;
                this.field2 = field2;
            }
        }

        String dataValue = "aaa1,aaa2;bbb1,bbb2;ccc1,ccc2";

        List<AnonymousClass> items = Arrays.stream(dataValue.split(";")).map(item -> {
            String[] fields = item.split(",");
            return new AnonymousClass(fields[0], fields[1]);
        }).collect(Collectors.toList());

        for (AnonymousClass item : items) {
            System.out.println(item.field1);
            System.out.println(item.field2);
            System.out.println();
        }
        */

        Pocito pocito = new Pocito("A", "B", 98);
        System.out.println("Pocito: " + pocito.toString());

        Pocito2 pocito2 = new Pocito2("AA", pocito);
        System.out.println("Pocito2: " + pocito2.toString());

        System.out.println(pocito2.getP().getB());
        for (Field field : Pocito2.class.getDeclaredFields()) {
            System.out.println(field);
        }
        Field pField = Pocito2.class.getDeclaredField("p");
        pField.setAccessible(true);
        System.out.println("Value of P: " + pField.get(pocito2));

        final String reflectionCall = "p.c";
        Object target = pocito2;
        target = getAuthValue(reflectionCall, target);
        System.out.println("%d" + target);
    }

    private static Object getAuthValue(final String path, Object target) throws NoSuchFieldException, IllegalAccessException {
        for (final String field : path.split("\\.")) {
            final Class targetClass = target.getClass();
            final Field targetField = targetClass.getDeclaredField(field);
            targetField.setAccessible(true);
            target = targetField.get(target);
        }
        return target;
    }

    /*
    public static Object retrieveObjectValue(Object obj, String property) {
        if (property.contains(".")) {
            // we need to recurse down to final object
            String props[] = property.split("\\.");
            try {
                Object ivalue = null;
                if (Map.class.isAssignableFrom(obj.getClass())) {
                    Map map = (Map) obj;
                    ivalue = map.get(props[0]);
                } else {
                    Method method = obj.getClass().getMethod(getGetterMethodName(props[0]));
                    ivalue = method.invoke(obj);
                }
                if (ivalue == null)
                    return null;
                return retrieveObjectValue(ivalue, property.substring(props[0].length() + 1));
            } catch (Exception e) {
                throw new RuntimeException("Failed to retrieve value for " + property, e);
            }
        } else {
            // let's get the object value directly
            try {
                if (Map.class.isAssignableFrom(obj.getClass())) {
                    Map map = (Map) obj;
                    return map.get(property);
                } else {
                    Method method = obj.getClass().getMethod(getGetterMethodName(property));
                    return method.invoke(obj);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to retrieve value for " + property, e);
            }
        }
    }
    */

    public static boolean foo() {
        System.out.println("Befostam!");
        return true;
    }
}
