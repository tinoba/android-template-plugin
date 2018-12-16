package main

data class MethodTypes(
        val shouldReadAll: Boolean,
        val shouldReadByPrimaryKeys: Boolean,
        val shouldUpdate: Boolean
)