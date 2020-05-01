package com.pv_libs.cachepro_rxjava.utils

import java.lang.reflect.*
import java.util.*


internal val Type.rawType: Type
    get() {
        return if (this is ParameterizedType) {
            rawType
        } else {
            this
        }
    }

internal val Type.childType: Type?
    get() {
        return if (this is ParameterizedType) {
            actualTypeArguments.getOrNull(0)
        } else null
    }

internal val Type.childRawType: Type?
    get() {
        return childType?.rawType
    }

internal fun Type.getClass(): Class<*> {
    Objects.requireNonNull(this, "type == null")
    if (this is Class<*>) {
        // Type is a normal class.
        return this
    }
    if (this is ParameterizedType) {

        // I'm not exactly sure why getRawType() returns Type instead of Class. Neal isn't either but
        // suspects some pathological case related to nested classes exists.
        val rawType = this.rawType
        require(rawType is Class<*>)
        return rawType
    }
    if (this is GenericArrayType) {
        val componentType =
            this.genericComponentType
        return java.lang.reflect.Array.newInstance(this.getClass(), 0).javaClass
    }
    if (this is TypeVariable<*>) {
        // We could use the variable's bounds, but that won't work if there are multiple. Having a raw
        // type that's more general than necessary is okay.
        return Any::class.java
    }
    if (this is WildcardType) {
        return this.upperBounds[0].getClass()
    }
    throw java.lang.IllegalArgumentException(
        "Expected a Class, ParameterizedType, or "
                + "GenericArrayType, but <" + this + "> is of type " + javaClass.name
    )
}


internal fun Type.wrapWith(parentType: Type): Type {
    return object : ParameterizedType {
        override fun getRawType(): Type {
            return parentType
        }

        override fun getOwnerType(): Type? {
            return null
        }

        override fun getActualTypeArguments(): Array<Type> {
            return arrayOf(this@wrapWith)
        }
    }
}
