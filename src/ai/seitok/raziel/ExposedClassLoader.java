package ai.seitok.raziel;

/**
 * ClassLoader to expose defineClass to the public
 */
public class ExposedClassLoader extends ClassLoader {

    public ExposedClassLoader(ClassLoader parent){
        super(parent);
    }

    // exposes the otherwise protected defineClass method
    public Class<?> defineClass(String name, byte[] bytes){
        return super.defineClass(name, bytes, 0, bytes.length);
    }

}
