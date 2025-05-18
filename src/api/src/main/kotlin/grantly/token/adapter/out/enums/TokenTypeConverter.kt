package grantly.token.adapter.out.enums

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class TokenTypeConverter : AttributeConverter<TokenType, Int> {
    override fun convertToDatabaseColumn(attribute: TokenType?): Int? = attribute?.type

    override fun convertToEntityAttribute(dbData: Int): TokenType = TokenType.from(dbData)
}
