package com.briarcraft.econ.util

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class JsonExclude

private val gsonExclusionStrategy: ExclusionStrategy = object : ExclusionStrategy {
    override fun shouldSkipClass(clazz: Class<*>?): Boolean {
        return false
    }

    override fun shouldSkipField(field: FieldAttributes): Boolean {
        return field.getAnnotation(JsonExclude::class.java) != null
    }
}

//fun gsonSerializeExclude() = GsonBuilder().addSerializationExclusionStrategy(gsonExclusionStrategy)
//fun gsonDeserializationExclude() = GsonBuilder().addDeserializationExclusionStrategy(gsonExclusionStrategy)
fun gsonWithExcludes(): GsonBuilder = GsonBuilder().setExclusionStrategies(gsonExclusionStrategy)
