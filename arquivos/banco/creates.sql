create table usuario
(
    id                                 serial               not null
        constraint usuario_pkey
            primary key,
    nome                               varchar(100)         not null,
    senha                              varchar(200)         not null,
    login                              varchar(20)          not null
        constraint usuario_uk
            unique,
    email                              varchar(100)         not null,
    telefone                           varchar(15),
    cpf_cnpj                           varchar(14),
    endereco varchar(250),
    numero integer,
    bairro varchar(150),
    cep varchar(9),
    cidade varchar(150),
    cod_ibge varchar(10),
    estado varchar(150),
    status                             varchar(20)          not null,
    data_expiracao_senha               timestamp            not null,
    senhas_anteriores                  varchar(200),
    data_ultimo_acesso                 timestamp,
    data_cadastro                      timestamp            not null,
    data_bloqueio                      timestamp,
    motivo_bloqueio                    varchar(20),
    motivo_desativacao                 varchar(20),
    data_expiracao_bloqueio            timestamp,
    data_atualizacao                   timestamp            not null,
    usuario_ultima_atualizacao_id      integer
        constraint usuario_fk1
            references usuario
            on delete set null
);

create table role
(
    id         serial      not null
        constraint role_pkey
            primary key,
    usuario_id integer
        constraint role_fk1
            references usuario
            on delete cascade,
    nome       varchar(30) not null,
    login      varchar(20) not null,
    constraint role_uk
        unique (usuario_id, nome)
);

create index role_idx1
    on role (usuario_id);

create table parametro
(
    id    serial        not null
        constraint parametro_pk
            primary key,
    chave varchar(50)   not null,
    valor varchar(6000) not null
);

create table produto
(
    id              serial       not null
        constraint produto_pk
            primary key,
    id_produto      varchar(100),
    cod             varchar(100),
    nome            varchar(300),
    descricao       varchar(500),
    gtin            varchar(100),
    cnm             varchar(100),
    cest            varchar(100),
    cfop            varchar(100),
    fornecedor_id   integer
        constraint produto_fk1
            references usuario
            on delete set null,
    estoque_atual   integer,
    estoque_minimo  integer,
    tempo_reposicao integer,
    unidade_medida  varchar(10),
    valor_unidade   bigint,
    origem_mercadoria  integer,
    aliquota_icms   bigint,
    aliquota_pis   bigint,
    aliquota_cofins   bigint,
    nome_imagem varchar(300),
    imagem_base64 text
);

create table orcamento
(
    id    serial        not null
        constraint orcamento_pk
            primary key,
    autor_id integer
        constraint orcamento_fk1
            references usuario
            on delete set null,
    cliente_id integer
        constraint orcamento_fk2
            references usuario
            on delete set null,
    forma_pagamento varchar(50),
    bandeira varchar(50)
);

create table orcamento_produto
(
    id    serial        not null
        constraint orcamento_produto_pk
            primary key,
    orcamento_id integer
        constraint orcamento_produto_fk1
            references orcamento
            on delete set null,
    produto_id integer
        constraint orcamento_produto_fk2
            references produto
            on delete set null,
    quantidade integer
);

create table nota_fiscal
(
    id                 serial not null
        constraint nota_fiscal_pk
            primary key,
    orcamento_id       integer
        constraint nota_fiscal_fk1
            references orcamento
            on delete set null,
    data_criacao       timestamp,
    data_envio         timestamp,
    status_nota_fiscal varchar(15),
    chave_acesso       varchar(44),
    protocolo varchar(30),
    xml                text,
    data_cancelamento      timestamp,
    protocolo_cancelamento varchar(30),
    xml_cancelamento    text
);

create unique index nota_fiscal_orcamento_id_uindex
    on nota_fiscal (orcamento_id);

create table sessao_http_request
(
    id         serial    not null,
    usuario_id integer,
    jsessionid varchar(100),
    data       timestamp not null,
    ativa      boolean   not null
);

create table movimentacao_produto
(
    id            serial
        constraint movimentacao_produto_pk
            primary key,
    data          timestamp,
    orcamento_id  integer
        constraint movimentacao_produto_fk1
            references orcamento,
    produto_id    integer
        constraint movimentacao_produto_fk2
            references produto,
    quantidade    integer,
    is_entrada    boolean not null,
    motivo_movimentacao        varchar(50),
    estoque_atual integer,
    valor_total   numeric(15, 2)
);

insert into parametro (chave, valor) values ('COR_BARRA','e60008'), ('COR_FONTE_TITULO_BARRA','6b6b6b'), ('COR_MENU','ffffff'), ('COR_FONTE_MENU','ffffff'), ('COR_FONTE_MENU_SELECIONADO','000000'), ('TITULO','EGY');