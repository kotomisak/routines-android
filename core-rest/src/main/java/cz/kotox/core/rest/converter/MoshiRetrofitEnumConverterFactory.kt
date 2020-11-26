package cz.kotox.core.rest.converter

import com.squareup.moshi.Json
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * Retrofit doesn't accept @Json annotations when used as parameter for retrofit.
 * https://medium.com/tedpark-developer/how-to-use-enum-with-retrofit-query-path-d9c14b93d68f
 *
 * This converter factory passed to Retrofit fix this issue
 */
class MoshiRetrofitEnumConverterFactory : Converter.Factory() {
	override fun stringConverter(
		type: Type,
		annotations: Array<Annotation>,
		retrofit: Retrofit
	): Converter<Enum<*>, String>? =
		if (type is Class<*> && type.isEnum) {
			Converter { enum ->
				try {
					enum.javaClass.getField(enum.name)
						.getAnnotation(Json::class.java)?.name
				} catch (exception: Exception) {
					null
				} ?: enum.toString()
			}
		} else {
			null
		}
}