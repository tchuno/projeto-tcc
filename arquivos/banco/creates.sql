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
    cpf                           varchar(11),
    rg                           varchar(15),
    endereco varchar(250),
    numero integer,
    bairro varchar(150),
    cep varchar(9),
    cidade varchar(150),
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
    descricao       varchar(500),
    gtin            varchar(100),
    cnm             varchar(100),
    cst             varchar(100),
    cest            varchar(100),
    fornecedor_id   integer
        constraint produto_fk1
            references usuario
            on delete set null,
    unidade_medica  varchar(50),
    estoque_minimo  integer,
    estoque_atual   integer,
    nome            varchar(300),
    tempo_reposicao integer,
    unidade_medida  varchar(10),
    valor_unidade   bigint
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
    forma_pagamento varchar(50)
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
            on delete set null
);

create table nota_fiscal
(
    id    serial        not null
        constraint nota_fiscal_pk
            primary key,
    orcamento_id integer
        constraint nota_fiscal_fk1
            references orcamento
            on delete set null,
    data_criacao timestamp,
    chave_acesso varchar(1000)
);