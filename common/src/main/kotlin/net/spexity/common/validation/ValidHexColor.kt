package net.spexity.common.validation

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [HexValidator::class])
annotation class ValidHexColor(
    val message: String = "must be a valid hex color",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

val HexRegex = "^#[A-Fa-f0-9]{6}$".toRegex()

class HexValidator : ConstraintValidator<ValidHexColor, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        return value != null && value.matches(HexRegex)
    }
}
