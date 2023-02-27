FROM jboss/wildfly:13.0.0.Final
LABEL authors="EGY"
LABEL email="alvessilva@ufpr.br"

USER root

RUN mkdir /opt/jboss/wildfly/modules/system/layers/base/org/postgresql
COPY ./postgresql/ /opt/jboss/wildfly/modules/system/layers/base/org/postgresql/

RUN mkdir /opt/gnfe
RUN mkdir /opt/gnfe/schemas
COPY ./schemas/ /opt/gnfe/schemas/
COPY ./cert-valido.pfx /opt/gnfe/

COPY ./standalone.xml /opt/jboss/wildfly/standalone/configuration

RUN /opt/jboss/wildfly/bin/add-user.sh admin Teste@123 --silent

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]