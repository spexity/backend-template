package net.spexity.backend.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.converter.xml.JacksonXmlHttpMessageConverter
import org.springframework.web.client.RestTemplate
import tools.jackson.dataformat.xml.XmlMapper

@Configuration
class RestTemplateConfig {

    @Bean
    fun restTemplate(): RestTemplate {
        val xmlConverter = JacksonXmlHttpMessageConverter(XmlMapper.builder().build())
        xmlConverter.supportedMediaTypes = listOf(MediaType.TEXT_XML, MediaType.APPLICATION_XML)

        return RestTemplate().apply {
            messageConverters.add(xmlConverter)
        }
    }

}
