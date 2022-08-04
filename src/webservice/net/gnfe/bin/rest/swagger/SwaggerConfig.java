package net.gnfe.bin.rest.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {

        ResponseMessage responseMessaHTTPGet200 = new ResponseMessageBuilder()
                .code(200)
                .message("Consulta realizada com sucesso.")
                .build();

        ResponseMessage responseMessaHTTPPost200 = new ResponseMessageBuilder()
                .code(200)
                .message("Operação realizada com sucesso.")
                .build();

        ResponseMessage responseMessaHTTP403 = new ResponseMessageBuilder()
                .code(403)
                .message("Usuário não tem permissão para acessar esse recurso.")
                .build();

        ResponseMessage responseMessaHTTP401 = new ResponseMessageBuilder()
                .code(401)
                .message("Usuário não autenticado. Faça login para obter um novo JSESSIONID.")
                .build();

        ResponseMessage responseMessaHTTP400 = new ResponseMessageBuilder()
                .code(400)
                .message("Ocorreu um erro ao processar a requisição.")
                .build();

        //Adding Header
        List<Parameter> aParameters = new ArrayList<Parameter>();
        aParameters.add(new ParameterBuilder().name("JSESSIONID").modelRef(new ModelRef("string")).parameterType("cookie").required(false).build());

        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("net.gnfe.bin.rest.controller"))
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(aParameters)
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.GET,
                        Arrays.asList(
                                responseMessaHTTPGet200,
                                responseMessaHTTP400,
                                responseMessaHTTP401,
                                responseMessaHTTP403
                        ))
                .globalResponseMessage(RequestMethod.POST,
                        Arrays.asList(
                                responseMessaHTTPPost200,
                                responseMessaHTTP400,
                                responseMessaHTTP401,
                                responseMessaHTTP403
                        ))
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "GNFE",
                "Catálogo de serviços RESTful/JSON.",
                "1.0",
                "Terms of service",
                new Contact("GNFE", null, ""),
                null, null, Collections.emptyList());
    }
}
