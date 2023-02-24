create table parametro
(
    id    serial,
    chave varchar(50)   not null,
    valor varchar(6000) not null,
    constraint parametro_pk
        primary key (id)
);

create table sessao_http_request
(
    id         serial,
    usuario_id integer,
    jsessionid varchar(100),
    data       timestamp not null,
    ativa      boolean   not null
);

create table usuario
(
    id                            serial,
    nome                          varchar(100) not null,
    senha                         varchar(200) not null,
    login                         varchar(20)  not null,
    email                         varchar(100) not null,
    telefone                      varchar(15),
    cpf_cnpj                      varchar(14),
    rg                            varchar(15),
    endereco                      varchar(250),
    numero                        integer,
    bairro                        varchar(150),
    cep                           varchar(9),
    cidade                        varchar(150),
    estado                        varchar(150),
    status                        varchar(20)  not null,
    data_expiracao_senha          timestamp    not null,
    senhas_anteriores             varchar(200),
    data_ultimo_acesso            timestamp,
    data_cadastro                 timestamp    not null,
    data_bloqueio                 timestamp,
    motivo_bloqueio               varchar(20),
    motivo_desativacao            varchar(20),
    data_expiracao_bloqueio       timestamp,
    data_atualizacao              timestamp    not null,
    usuario_ultima_atualizacao_id integer,
    cod_ibge                      varchar(10),
    primary key (id),
    constraint usuario_uk
        unique (login),
    constraint usuario_fk1
        foreign key (usuario_ultima_atualizacao_id) references usuario
            on delete set null
);

create table orcamento
(
    id              serial,
    autor_id        integer,
    cliente_id      integer,
    forma_pagamento varchar(50),
    bandeira        varchar(50),
    constraint orcamento_pk
        primary key (id),
    constraint orcamento_fk1
        foreign key (autor_id) references usuario,
    constraint orcamento_fk2
        foreign key (cliente_id) references usuario
);

create table nota_fiscal
(
    id                     serial,
    orcamento_id           integer,
    data_criacao           timestamp,
    chave_acesso           varchar(44),
    xml                    text,
    status_nota_fiscal     varchar(15),
    data_envio             timestamp,
    protocolo              varchar(30),
    xml_cancelamento       text,
    protocolo_cancelamento varchar(30),
    data_cancelamento      timestamp,
    constraint nota_fiscal_pk
        primary key (id),
    constraint nota_fiscal_pk2
        unique (orcamento_id),
    constraint nota_fiscal_fk1
        foreign key (orcamento_id) references orcamento
            on delete cascade
);

create table produto
(
    id                serial,
    cod               varchar(100),
    descricao         varchar(500),
    gtin              varchar(100),
    cnm               varchar(100),
    cest              varchar(100),
    fornecedor_id     integer,
    estoque_minimo    integer,
    estoque           integer,
    nome              varchar(300),
    tempo_reposicao   integer,
    unidade_medida    varchar(10),
    valor_unidade     numeric(15, 2),
    nome_imagem       varchar(300),
    imagem_base64     text,
    estoque_atual     integer,
    cfop              varchar(100),
    origem_mercadoria varchar(10),
    aliquota_icms     bigint,
    aliquota_pis      bigint,
    aliquota_cofins   bigint,
    constraint produto_pk
        primary key (id),
    constraint produto_fk1
        foreign key (fornecedor_id) references usuario
            on delete set null
);

create table orcamento_produto
(
    id           serial,
    orcamento_id integer,
    produto_id   integer,
    quantidade   integer default 1 not null,
    constraint orcamento_produto_pk
        primary key (id),
    constraint orcamento_produto_fk1
        foreign key (orcamento_id) references orcamento
            on delete cascade,
    constraint orcamento_produto_fk2
        foreign key (produto_id) references produto
);

create table role
(
    id         serial,
    usuario_id integer,
    nome       varchar(30) not null,
    login      varchar(20) not null,
    primary key (id),
    constraint role_uk
        unique (usuario_id, nome),
    constraint role_fk1
        foreign key (usuario_id) references usuario
            on delete cascade
);

create table movimentacao_produto
(
    id                  serial,
    data                timestamp,
    orcamento_id        integer,
    produto_id          integer,
    quantidade          integer,
    is_entrada          boolean not null,
    motivo_movimentacao varchar(50),
    estoque_atual       integer,
    valor_total         numeric(15, 2),
    constraint movimentacao_produto_pk
        primary key (id),
    constraint movimentacao_produto_fk1
        foreign key (orcamento_id) references orcamento,
    constraint movimentacao_produto_fk2
        foreign key (produto_id) references produto
);

insert into parametro (chave, valor) values ('COR_BARRA','e60008'), ('COR_FONTE_TITULO_BARRA','6b6b6b'), ('COR_MENU','ffffff'), ('COR_FONTE_MENU','ffffff'), ('COR_FONTE_MENU_SELECIONADO','000000'), ('TITULO','EGY');