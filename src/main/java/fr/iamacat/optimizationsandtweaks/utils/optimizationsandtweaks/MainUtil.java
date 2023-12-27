package fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Map;
import java.util.Vector;

public class MainUtil {

    public static final java.lang.reflect.Type field_152370_a = new ParameterizedType() {

        public java.lang.reflect.Type[] getActualTypeArguments() {
            return new java.lang.reflect.Type[] { String.class, new ParameterizedType() {

                public java.lang.reflect.Type[] getActualTypeArguments() {
                    return new java.lang.reflect.Type[] { String.class };
                }

                public java.lang.reflect.Type getRawType() {
                    return Collection.class;
                }

                public java.lang.reflect.Type getOwnerType() {
                    return null;
                }
            } };
        }

        public java.lang.reflect.Type getRawType() {
            return Map.class;
        }

        public java.lang.reflect.Type getOwnerType() {
            return null;
        }
    };
}
