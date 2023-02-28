--creates para criar database e usuário

CREATE DATABASE gnfe;
CREATE ROLE gnfe WITH LOGIN SUPERUSER PASSWORD 'gnfe';
ALTER DATABASE gnfe OWNER TO gnfe;
GRANT ALL PRIVILEGES ON DATABASE gnfe TO gnfe;

--creates para inclir na base de dados gnfe

CREATE TABLE public.parametro (
                                  id serial NOT NULL,
                                  chave varchar(50) NOT NULL,
                                  valor varchar(6000) NOT NULL,
                                  CONSTRAINT parametro_pk PRIMARY KEY (id)
);

CREATE TABLE public.produto (
                                id serial NOT NULL,
                                cod varchar(100) DEFAULT NULL,
                                nome varchar(300) DEFAULT NULL,
                                descricao varchar(500) DEFAULT NULL,
                                gtin varchar(100) DEFAULT NULL,
                                cnm varchar(100) DEFAULT NULL,
                                cest varchar(100) DEFAULT NULL,
                                cfop varchar(100) DEFAULT NULL,
                                estoque_atual integer DEFAULT NULL,
                                estoque_minimo integer DEFAULT NULL,
                                tempo_reposicao integer DEFAULT NULL,
                                valor_compra numeric(15, 2) DEFAULT NULL,
                                valor_unidade numeric(15, 2) DEFAULT NULL,
                                unidade_medida varchar(10) DEFAULT NULL,
                                origem_mercadoria varchar(10) DEFAULT NULL,
                                aliquota_icms bigint DEFAULT NULL,
                                aliquota_pis bigint DEFAULT NULL,
                                aliquota_cofins bigint DEFAULT NULL,
                                nome_imagem varchar(300) DEFAULT NULL,
                                imagem_base64 text DEFAULT NULL,
                                CONSTRAINT produto_pk PRIMARY KEY (id)
);

CREATE TABLE public.sessao_http_request (
                                            id serial NOT NULL,
                                            usuario_id integer DEFAULT NULL,
                                            jsessionid varchar(100) DEFAULT NULL,
                                            data timestamp NOT NULL,
                                            ativa bool NOT NULL,
                                            PRIMARY KEY (id)
);

CREATE TABLE public.usuario (
                                id serial NOT NULL,
                                login varchar(20) NOT NULL,
                                nome varchar(100) NOT NULL,
                                senha varchar(200) NOT NULL,
                                email varchar(100) NOT NULL,
                                telefone varchar(15) DEFAULT NULL,
                                cpf_cnpj varchar(14) DEFAULT NULL,
                                rg varchar(15) DEFAULT NULL,
                                endereco varchar(250) DEFAULT NULL,
                                numero integer DEFAULT NULL,
                                bairro varchar(150) DEFAULT NULL,
                                cep varchar(9) DEFAULT NULL,
                                cidade varchar(150) DEFAULT NULL,
                                cod_ibge varchar(10) DEFAULT NULL,
                                estado varchar(150) DEFAULT NULL,
                                status varchar(20) NOT NULL,
                                data_expiracao_senha timestamp NOT NULL,
                                senhas_anteriores varchar(200) DEFAULT NULL,
                                data_ultimo_acesso timestamp DEFAULT NULL,
                                data_cadastro timestamp NOT NULL,
                                data_bloqueio timestamp DEFAULT NULL,
                                motivo_bloqueio varchar(20) DEFAULT NULL,
                                motivo_desativacao varchar(20) DEFAULT NULL,
                                data_expiracao_bloqueio timestamp DEFAULT NULL,
                                data_atualizacao timestamp NOT NULL,
                                usuario_ultima_atualizacao_id integer DEFAULT NULL,
                                CONSTRAINT usuario_pkey PRIMARY KEY (id),
                                CONSTRAINT usuario_fk1 FOREIGN KEY (usuario_ultima_atualizacao_id) REFERENCES usuario (id) ON DELETE
                                    SET
                                    NULL
);
CREATE UNIQUE INDEX usuario_uk ON public.usuario (login);

CREATE TABLE public.orcamento (
                                  id serial NOT NULL,
                                  cliente_id integer NOT NULL,
                                  data_criacao timestamp DEFAULT NULL,
                                  forma_pagamento varchar(50) DEFAULT NULL,
                                  bandeira varchar(50) DEFAULT NULL,
                                  CONSTRAINT orcamento_pk PRIMARY KEY (id),
                                  CONSTRAINT orcamento_fk2 FOREIGN KEY (cliente_id) REFERENCES usuario (id)
);

CREATE TABLE public.role (
                             id serial NOT NULL,
                             usuario_id integer NOT NULL,
                             nome varchar(30) NOT NULL,
                             login varchar(20) NOT NULL,
                             CONSTRAINT role_pkey PRIMARY KEY (id),
                             CONSTRAINT role_fk1 FOREIGN KEY (usuario_id) REFERENCES usuario (id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX role_uk ON public.role (usuario_id, nome);

CREATE TABLE public.nota_fiscal (
                                    id serial NOT NULL,
                                    orcamento_id integer NOT NULL,
                                    status_nota_fiscal varchar(15) DEFAULT NULL,
                                    data_envio timestamp DEFAULT NULL,
                                    protocolo varchar(30) DEFAULT NULL,
                                    chave_acesso varchar(44) DEFAULT NULL,
                                    xml text DEFAULT NULL,
                                    data_cancelamento timestamp DEFAULT NULL,
                                    protocolo_cancelamento varchar(30) DEFAULT NULL,
                                    xml_cancelamento text DEFAULT NULL,
                                    CONSTRAINT nota_fiscal_pk PRIMARY KEY (id),
                                    CONSTRAINT nota_fiscal_fk1 FOREIGN KEY (orcamento_id) REFERENCES orcamento (id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX nota_fiscal_pk2 ON public.nota_fiscal (orcamento_id);

CREATE TABLE public.orcamento_produto (
                                          id serial NOT NULL,
                                          orcamento_id integer NOT NULL,
                                          produto_id integer NOT NULL,
                                          quantidade integer DEFAULT 1 NOT NULL,
                                          CONSTRAINT orcamento_produto_pk PRIMARY KEY (id),
                                          CONSTRAINT orcamento_produto_fk1 FOREIGN KEY (orcamento_id) REFERENCES orcamento (id) ON DELETE CASCADE,
                                          CONSTRAINT orcamento_produto_fk2 FOREIGN KEY (produto_id) REFERENCES produto (id)
);

CREATE TABLE public.movimentacao_produto (
                                             id serial NOT NULL,
                                             orcamento_produto_id integer DEFAULT NULL,
                                             fornecedor_id integer DEFAULT NULL,
                                             quantidade integer DEFAULT NULL,
                                             qtd_estoque integer DEFAULT NULL,
                                             valor_total numeric(15, 2) DEFAULT NULL,
                                             data timestamp NOT NULL,
                                             autor_id integer NOT NULL,
                                             is_entrada bool NOT NULL,
                                             motivo_movimentacao varchar(50) NOT NULL,
                                             valor_icms numeric(15, 2) NULL,
                                             valor_pis numeric(15, 2) NULL,
                                             valor_cofins numeric(15, 2) NULL,
                                             CONSTRAINT movimentacao_produto_pk PRIMARY KEY (id),
                                             CONSTRAINT movimentacao_produto_fk1 FOREIGN KEY (orcamento_produto_id) REFERENCES orcamento_produto (id),
                                             CONSTRAINT movimentacao_produto_fk2 FOREIGN KEY (fornecedor_id) REFERENCES usuario (id),
                                             CONSTRAINT movimentacao_produto_fk3 FOREIGN KEY (autor_id) REFERENCES usuario (id)
);

insert into parametro (chave, valor) values ('COR_BARRA','e60008'), ('COR_FONTE_TITULO_BARRA','6b6b6b'), ('COR_MENU','ffffff'), ('COR_FONTE_MENU','ffffff'), ('COR_FONTE_MENU_SELECIONADO','000000'), ('TITULO','EGY');