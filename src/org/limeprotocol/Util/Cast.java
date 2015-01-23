package org.limeprotocol.Util;

public class Cast {

    /**
     * C# as-operator JAVA implementation
     * http://stackoverflow.com/questions/148828/how-to-emulate-c-sharp-as-operator-in-java
    */
    public static <T> T as(Class<T> clazz, Object o){
        if(clazz.isInstance(o)){
            return clazz.cast(o);
        }
        return null;
    }
}
