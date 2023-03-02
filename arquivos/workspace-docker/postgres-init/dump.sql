--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.21
-- Dumped by pg_dump version 9.6.21

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: movimentacao_produto; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.movimentacao_produto (
    id integer NOT NULL,
    orcamento_produto_id integer,
    produto_id integer,
    fornecedor_id integer,
    quantidade integer,
    qtd_estoque integer,
    valor_total numeric(15,2) DEFAULT NULL::numeric,
    data timestamp without time zone NOT NULL,
    autor_id integer NOT NULL,
    is_entrada boolean NOT NULL,
    motivo_movimentacao character varying(50) NOT NULL,
    valor_icms numeric(15,2) DEFAULT NULL::numeric,
    valor_pis numeric(15,2) DEFAULT NULL::numeric,
    valor_cofins numeric(15,2) DEFAULT NULL::numeric
);


ALTER TABLE public.movimentacao_produto OWNER TO postgres;

--
-- Name: movimentacao_produto_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.movimentacao_produto_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.movimentacao_produto_id_seq OWNER TO postgres;

--
-- Name: movimentacao_produto_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.movimentacao_produto_id_seq OWNED BY public.movimentacao_produto.id;


--
-- Name: nota_fiscal; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.nota_fiscal (
    id integer NOT NULL,
    orcamento_id integer NOT NULL,
    status_nota_fiscal character varying(15) DEFAULT NULL::character varying,
    data_envio timestamp without time zone,
    protocolo character varying(30) DEFAULT NULL::character varying,
    chave_acesso character varying(44) DEFAULT NULL::character varying,
    xml text,
    data_cancelamento timestamp without time zone,
    protocolo_cancelamento character varying(30) DEFAULT NULL::character varying,
    xml_cancelamento text
);


ALTER TABLE public.nota_fiscal OWNER TO postgres;

--
-- Name: nota_fiscal_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.nota_fiscal_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.nota_fiscal_id_seq OWNER TO postgres;

--
-- Name: nota_fiscal_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.nota_fiscal_id_seq OWNED BY public.nota_fiscal.id;


--
-- Name: orcamento; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.orcamento (
    id integer NOT NULL,
    cliente_id integer NOT NULL,
    data_criacao timestamp without time zone,
    forma_pagamento character varying(50) DEFAULT NULL::character varying,
    bandeira character varying(50) DEFAULT NULL::character varying
);


ALTER TABLE public.orcamento OWNER TO postgres;

--
-- Name: orcamento_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.orcamento_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.orcamento_id_seq OWNER TO postgres;

--
-- Name: orcamento_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.orcamento_id_seq OWNED BY public.orcamento.id;


--
-- Name: orcamento_produto; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.orcamento_produto (
    id integer NOT NULL,
    orcamento_id integer NOT NULL,
    produto_id integer NOT NULL,
    quantidade integer DEFAULT 1 NOT NULL
);


ALTER TABLE public.orcamento_produto OWNER TO postgres;

--
-- Name: orcamento_produto_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.orcamento_produto_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.orcamento_produto_id_seq OWNER TO postgres;

--
-- Name: orcamento_produto_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.orcamento_produto_id_seq OWNED BY public.orcamento_produto.id;


--
-- Name: parametro; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.parametro (
    id integer NOT NULL,
    chave character varying(50) NOT NULL,
    valor character varying(6000) NOT NULL
);


ALTER TABLE public.parametro OWNER TO postgres;

--
-- Name: parametro_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.parametro_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.parametro_id_seq OWNER TO postgres;

--
-- Name: parametro_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.parametro_id_seq OWNED BY public.parametro.id;


--
-- Name: produto; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.produto (
    id integer NOT NULL,
    cod character varying(100) DEFAULT NULL::character varying,
    nome character varying(300) DEFAULT NULL::character varying,
    descricao character varying(500) DEFAULT NULL::character varying,
    gtin character varying(100) DEFAULT NULL::character varying,
    cnm character varying(100) DEFAULT NULL::character varying,
    cest character varying(100) DEFAULT NULL::character varying,
    cfop character varying(100) DEFAULT NULL::character varying,
    estoque_atual integer,
    estoque_minimo integer,
    tempo_reposicao integer,
    valor_compra numeric(15,2) DEFAULT NULL::numeric,
    valor_unidade numeric(15,2) DEFAULT NULL::numeric,
    unidade_medida character varying(10) DEFAULT NULL::character varying,
    origem_mercadoria character varying(10) DEFAULT NULL::character varying,
    aliquota_icms bigint,
    aliquota_pis bigint,
    aliquota_cofins bigint,
    nome_imagem character varying(300) DEFAULT NULL::character varying,
    imagem_base64 text
);


ALTER TABLE public.produto OWNER TO postgres;

--
-- Name: produto_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.produto_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.produto_id_seq OWNER TO postgres;

--
-- Name: produto_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.produto_id_seq OWNED BY public.produto.id;


--
-- Name: role; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.role (
    id integer NOT NULL,
    usuario_id integer NOT NULL,
    nome character varying(30) NOT NULL,
    login character varying(20) NOT NULL
);


ALTER TABLE public.role OWNER TO postgres;

--
-- Name: role_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.role_id_seq OWNER TO postgres;

--
-- Name: role_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.role_id_seq OWNED BY public.role.id;


--
-- Name: sessao_http_request; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.sessao_http_request (
    id integer NOT NULL,
    usuario_id integer,
    jsessionid character varying(100) DEFAULT NULL::character varying,
    data timestamp without time zone NOT NULL,
    ativa boolean NOT NULL
);


ALTER TABLE public.sessao_http_request OWNER TO postgres;

--
-- Name: sessao_http_request_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.sessao_http_request_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.sessao_http_request_id_seq OWNER TO postgres;

--
-- Name: sessao_http_request_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.sessao_http_request_id_seq OWNED BY public.sessao_http_request.id;


--
-- Name: usuario; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.usuario (
    id integer NOT NULL,
    login character varying(20) NOT NULL,
    nome character varying(100) NOT NULL,
    senha character varying(200) NOT NULL,
    email character varying(100) NOT NULL,
    telefone character varying(15) DEFAULT NULL::character varying,
    cpf_cnpj character varying(14) DEFAULT NULL::character varying,
    rg character varying(15) DEFAULT NULL::character varying,
    endereco character varying(250) DEFAULT NULL::character varying,
    numero integer,
    bairro character varying(150) DEFAULT NULL::character varying,
    cep character varying(9) DEFAULT NULL::character varying,
    cidade character varying(150) DEFAULT NULL::character varying,
    cod_ibge character varying(10) DEFAULT NULL::character varying,
    estado character varying(150) DEFAULT NULL::character varying,
    status character varying(20) NOT NULL,
    data_expiracao_senha timestamp without time zone NOT NULL,
    senhas_anteriores character varying(200) DEFAULT NULL::character varying,
    data_ultimo_acesso timestamp without time zone,
    data_cadastro timestamp without time zone NOT NULL,
    data_bloqueio timestamp without time zone,
    motivo_bloqueio character varying(20) DEFAULT NULL::character varying,
    motivo_desativacao character varying(20) DEFAULT NULL::character varying,
    data_expiracao_bloqueio timestamp without time zone,
    data_atualizacao timestamp without time zone NOT NULL,
    usuario_ultima_atualizacao_id integer
);


ALTER TABLE public.usuario OWNER TO postgres;

--
-- Name: usuario_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.usuario_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.usuario_id_seq OWNER TO postgres;

--
-- Name: usuario_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.usuario_id_seq OWNED BY public.usuario.id;


--
-- Name: movimentacao_produto id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.movimentacao_produto ALTER COLUMN id SET DEFAULT nextval('public.movimentacao_produto_id_seq'::regclass);


--
-- Name: nota_fiscal id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nota_fiscal ALTER COLUMN id SET DEFAULT nextval('public.nota_fiscal_id_seq'::regclass);


--
-- Name: orcamento id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.orcamento ALTER COLUMN id SET DEFAULT nextval('public.orcamento_id_seq'::regclass);


--
-- Name: orcamento_produto id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.orcamento_produto ALTER COLUMN id SET DEFAULT nextval('public.orcamento_produto_id_seq'::regclass);


--
-- Name: parametro id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.parametro ALTER COLUMN id SET DEFAULT nextval('public.parametro_id_seq'::regclass);


--
-- Name: produto id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.produto ALTER COLUMN id SET DEFAULT nextval('public.produto_id_seq'::regclass);


--
-- Name: role id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.role ALTER COLUMN id SET DEFAULT nextval('public.role_id_seq'::regclass);


--
-- Name: sessao_http_request id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sessao_http_request ALTER COLUMN id SET DEFAULT nextval('public.sessao_http_request_id_seq'::regclass);


--
-- Name: usuario id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuario ALTER COLUMN id SET DEFAULT nextval('public.usuario_id_seq'::regclass);


--
-- Data for Name: movimentacao_produto; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.movimentacao_produto (id, orcamento_produto_id, produto_id, fornecedor_id, quantidade, qtd_estoque, valor_total, data, autor_id, is_entrada, motivo_movimentacao, valor_icms, valor_pis, valor_cofins) FROM stdin;
\.


--
-- Name: movimentacao_produto_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.movimentacao_produto_id_seq', 1, false);


--
-- Data for Name: nota_fiscal; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.nota_fiscal (id, orcamento_id, status_nota_fiscal, data_envio, protocolo, chave_acesso, xml, data_cancelamento, protocolo_cancelamento, xml_cancelamento) FROM stdin;
\.


--
-- Name: nota_fiscal_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.nota_fiscal_id_seq', 1, false);


--
-- Data for Name: orcamento; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.orcamento (id, cliente_id, data_criacao, forma_pagamento, bandeira) FROM stdin;
\.


--
-- Name: orcamento_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.orcamento_id_seq', 1, false);


--
-- Data for Name: orcamento_produto; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.orcamento_produto (id, orcamento_id, produto_id, quantidade) FROM stdin;
\.


--
-- Name: orcamento_produto_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.orcamento_produto_id_seq', 1, false);


--
-- Data for Name: parametro; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.parametro (id, chave, valor) FROM stdin;
1	COR_BARRA	e60008
2	COR_FONTE_TITULO_BARRA	6b6b6b
3	COR_MENU	ffffff
4	COR_FONTE_MENU	ffffff
5	COR_FONTE_MENU_SELECIONADO	000000
6	TITULO	EGY
\.


--
-- Name: parametro_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.parametro_id_seq', 6, true);


--
-- Data for Name: produto; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.produto (id, cod, nome, descricao, gtin, cnm, cest, cfop, estoque_atual, estoque_minimo, tempo_reposicao, valor_compra, valor_unidade, unidade_medida, origem_mercadoria, aliquota_icms, aliquota_pis, aliquota_cofins, nome_imagem, imagem_base64) FROM stdin;
\.


--
-- Name: produto_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.produto_id_seq', 1, false);


--
-- Data for Name: role; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.role (id, usuario_id, nome, login) FROM stdin;
\.


--
-- Name: role_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.role_id_seq', 1, false);


--
-- Data for Name: sessao_http_request; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.sessao_http_request (id, usuario_id, jsessionid, data, ativa) FROM stdin;
\.


--
-- Name: sessao_http_request_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.sessao_http_request_id_seq', 1, false);


--
-- Data for Name: usuario; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.usuario (id, login, nome, senha, email, telefone, cpf_cnpj, rg, endereco, numero, bairro, cep, cidade, cod_ibge, estado, status, data_expiracao_senha, senhas_anteriores, data_ultimo_acesso, data_cadastro, data_bloqueio, motivo_bloqueio, motivo_desativacao, data_expiracao_bloqueio, data_atualizacao, usuario_ultima_atualizacao_id) FROM stdin;
\.


--
-- Name: usuario_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.usuario_id_seq', 1, false);


--
-- Name: movimentacao_produto movimentacao_produto_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.movimentacao_produto
    ADD CONSTRAINT movimentacao_produto_pk PRIMARY KEY (id);


--
-- Name: nota_fiscal nota_fiscal_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nota_fiscal
    ADD CONSTRAINT nota_fiscal_pk PRIMARY KEY (id);


--
-- Name: orcamento orcamento_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.orcamento
    ADD CONSTRAINT orcamento_pk PRIMARY KEY (id);


--
-- Name: orcamento_produto orcamento_produto_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.orcamento_produto
    ADD CONSTRAINT orcamento_produto_pk PRIMARY KEY (id);


--
-- Name: parametro parametro_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.parametro
    ADD CONSTRAINT parametro_pk PRIMARY KEY (id);


--
-- Name: produto produto_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.produto
    ADD CONSTRAINT produto_pk PRIMARY KEY (id);


--
-- Name: role role_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.role
    ADD CONSTRAINT role_pkey PRIMARY KEY (id);


--
-- Name: sessao_http_request sessao_http_request_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sessao_http_request
    ADD CONSTRAINT sessao_http_request_pkey PRIMARY KEY (id);


--
-- Name: usuario usuario_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT usuario_pkey PRIMARY KEY (id);


--
-- Name: nota_fiscal_pk2; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX nota_fiscal_pk2 ON public.nota_fiscal USING btree (orcamento_id);


--
-- Name: role_uk; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX role_uk ON public.role USING btree (usuario_id, nome);


--
-- Name: usuario_uk; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX usuario_uk ON public.usuario USING btree (login);


--
-- Name: movimentacao_produto movimentacao_produto_fk1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.movimentacao_produto
    ADD CONSTRAINT movimentacao_produto_fk1 FOREIGN KEY (orcamento_produto_id) REFERENCES public.orcamento_produto(id);


--
-- Name: movimentacao_produto movimentacao_produto_fk2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.movimentacao_produto
    ADD CONSTRAINT movimentacao_produto_fk2 FOREIGN KEY (fornecedor_id) REFERENCES public.usuario(id);


--
-- Name: movimentacao_produto movimentacao_produto_fk3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.movimentacao_produto
    ADD CONSTRAINT movimentacao_produto_fk3 FOREIGN KEY (autor_id) REFERENCES public.usuario(id);


--
-- Name: movimentacao_produto movimentacao_produto_fk4; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.movimentacao_produto
    ADD CONSTRAINT movimentacao_produto_fk4 FOREIGN KEY (produto_id) REFERENCES public.produto(id);


--
-- Name: nota_fiscal nota_fiscal_fk1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nota_fiscal
    ADD CONSTRAINT nota_fiscal_fk1 FOREIGN KEY (orcamento_id) REFERENCES public.orcamento(id) ON DELETE CASCADE;


--
-- Name: orcamento orcamento_fk1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.orcamento
    ADD CONSTRAINT orcamento_fk1 FOREIGN KEY (cliente_id) REFERENCES public.usuario(id);


--
-- Name: orcamento_produto orcamento_produto_fk1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.orcamento_produto
    ADD CONSTRAINT orcamento_produto_fk1 FOREIGN KEY (orcamento_id) REFERENCES public.orcamento(id) ON DELETE CASCADE;


--
-- Name: orcamento_produto orcamento_produto_fk2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.orcamento_produto
    ADD CONSTRAINT orcamento_produto_fk2 FOREIGN KEY (produto_id) REFERENCES public.produto(id);


--
-- Name: role role_fk1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.role
    ADD CONSTRAINT role_fk1 FOREIGN KEY (usuario_id) REFERENCES public.usuario(id) ON DELETE CASCADE;


--
-- Name: usuario usuario_fk1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT usuario_fk1 FOREIGN KEY (usuario_ultima_atualizacao_id) REFERENCES public.usuario(id) ON DELETE SET NULL;


--
-- PostgreSQL database dump complete
--

