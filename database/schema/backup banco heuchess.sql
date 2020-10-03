--
-- PostgreSQL database dump
--

-- Dumped from database version 9.2.1
-- Dumped by pg_dump version 9.2.1
-- Started on 2012-11-22 14:32:05

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 185 (class 3079 OID 11727)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 2136 (class 0 OID 0)
-- Dependencies: 185
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 168 (class 1259 OID 16397)
-- Name: heu_avaliacoes; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE heu_avaliacoes (
    cod_avaliacao bigint NOT NULL,
    cod_usuario bigint NOT NULL,
    cod_situacao bigint NOT NULL,
    cod_tipo bigint NOT NULL,
    cod_componente bigint NOT NULL,
    aplicavel boolean NOT NULL,
    condicao_regra text NOT NULL,
    valor_tabuleiro double precision,
    data_modificacao date DEFAULT now() NOT NULL,
    data_criacao date DEFAULT now() NOT NULL,
    comando_aplicavel text NOT NULL,
    valor_peao double precision DEFAULT 0,
    valor_torre double precision DEFAULT 0,
    valor_cavalo double precision DEFAULT 0,
    valor_bispo double precision DEFAULT 0,
    valor_dama double precision DEFAULT 0,
    cod_conjunto bigint NOT NULL,
    valor_brancas double precision DEFAULT 0,
    valor_pretas double precision DEFAULT 0
);


ALTER TABLE public.heu_avaliacoes OWNER TO postgres;

--
-- TOC entry 169 (class 1259 OID 16412)
-- Name: heu_classe; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE heu_classe (
    cod_classe bigint NOT NULL,
    txt_nome character varying NOT NULL,
    txt_descricao character varying NOT NULL,
    CONSTRAINT ckc_txt_nome_heu_clas CHECK (((txt_nome)::text = upper((txt_nome)::text)))
);


ALTER TABLE public.heu_classe OWNER TO postgres;

--
-- TOC entry 170 (class 1259 OID 16419)
-- Name: heu_componente; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE heu_componente (
    cod_componente bigint NOT NULL,
    cod_usuario bigint NOT NULL,
    cod_tipo bigint NOT NULL,
    txt_nome character varying NOT NULL,
    txt_definicao character varying NOT NULL,
    num_versao bigint DEFAULT 1 NOT NULL,
    dat_criacao timestamp without time zone DEFAULT now() NOT NULL,
    dat_ultima_modificacao timestamp without time zone DEFAULT now() NOT NULL,
    qtd_acessos bigint DEFAULT 0 NOT NULL,
    qtd_copias bigint DEFAULT 0 NOT NULL,
    qtd_anotacoes_recebidas bigint DEFAULT 0 NOT NULL,
    bit_permissoes smallint DEFAULT 0 NOT NULL,
    CONSTRAINT ckc_bit_permissoes_ac_heu_defi CHECK ((bit_permissoes >= 0)),
    CONSTRAINT ckc_num_versao_heu_defi CHECK ((num_versao >= 1)),
    CONSTRAINT ckc_qtd_acessos_heu_defi CHECK ((qtd_acessos >= 0)),
    CONSTRAINT ckc_qtd_anotacoes_rec_heu_defi CHECK ((qtd_anotacoes_recebidas >= 0)),
    CONSTRAINT ckc_qtd_copias_heu_defi CHECK ((qtd_copias >= 0)),
    CONSTRAINT ckc_txt_nome_heu_defi CHECK (((txt_nome)::text = upper((txt_nome)::text)))
);


ALTER TABLE public.heu_componente OWNER TO postgres;

--
-- TOC entry 171 (class 1259 OID 16438)
-- Name: heu_componente_componente; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE heu_componente_componente (
    cod_componente_principal bigint NOT NULL,
    cod_componente_incluido bigint NOT NULL,
    dat_inclusao timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.heu_componente_componente OWNER TO postgres;

--
-- TOC entry 172 (class 1259 OID 16442)
-- Name: heu_desempenho_conj_heu; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE heu_desempenho_conj_heu (
    cod_componente bigint NOT NULL,
    qtd_competicoes_disputadas bigint DEFAULT 0 NOT NULL,
    qtd_partidas_ganhas bigint DEFAULT 0 NOT NULL,
    qtd_partidas_perdidas bigint DEFAULT 0 NOT NULL,
    qtd_partidas_empatadas bigint DEFAULT 0 NOT NULL,
    CONSTRAINT ckc_qtd_competicoes_d_heu_conj CHECK ((qtd_competicoes_disputadas >= 0)),
    CONSTRAINT ckc_qtd_partidas_empa_heu_conj CHECK ((qtd_partidas_empatadas >= 0)),
    CONSTRAINT ckc_qtd_partidas_ganh_heu_conj CHECK ((qtd_partidas_ganhas >= 0)),
    CONSTRAINT ckc_qtd_partidas_perd_heu_conj CHECK ((qtd_partidas_perdidas >= 0))
);


ALTER TABLE public.heu_desempenho_conj_heu OWNER TO postgres;

--
-- TOC entry 183 (class 1259 OID 16694)
-- Name: heu_historico; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE heu_historico (
    cod_historico bigint NOT NULL,
    cod_usuario bigint NOT NULL,
    cod_tipo bigint NOT NULL,
    txt_descricao text,
    cod_componente bigint,
    cod_tipo_componente bigint,
    cod_autor_componente bigint,
    dat_criacao timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.heu_historico OWNER TO postgres;

--
-- TOC entry 173 (class 1259 OID 16453)
-- Name: heu_instituicao; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE heu_instituicao (
    cod_instituicao bigint NOT NULL,
    txt_nome character varying NOT NULL,
    dat_criacao timestamp with time zone DEFAULT now() NOT NULL,
    dat_cancelamento timestamp with time zone,
    txt_descricao character varying NOT NULL,
    cod_usuario bigint NOT NULL,
    CONSTRAINT ckc_txt_nome_heu_instit CHECK (((txt_nome)::text = upper((txt_nome)::text)))
);


ALTER TABLE public.heu_instituicao OWNER TO postgres;

--
-- TOC entry 174 (class 1259 OID 16461)
-- Name: heu_situacoesdejogo; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE heu_situacoesdejogo (
    cod_situacao integer NOT NULL,
    cod_usuario bigint NOT NULL,
    fen character varying(255) NOT NULL,
    data_criacao date DEFAULT now() NOT NULL,
    data_modificacao date DEFAULT now(),
    classificacao smallint DEFAULT 0 NOT NULL,
    txt_definicao text,
    etapa_geral integer DEFAULT 0 NOT NULL,
    CONSTRAINT "CK_Classificacao" CHECK ((classificacao = ANY (ARRAY[0, 1, 2, 3, 4, 5, 6])))
);


ALTER TABLE public.heu_situacoesdejogo OWNER TO postgres;

--
-- TOC entry 2144 (class 0 OID 0)
-- Dependencies: 174
-- Name: TABLE heu_situacoesdejogo; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE heu_situacoesdejogo IS 'Tabela para armazenar as situações de jogo analisadas por um usuário.';


--
-- TOC entry 2145 (class 0 OID 0)
-- Dependencies: 174
-- Name: COLUMN heu_situacoesdejogo.classificacao; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN heu_situacoesdejogo.classificacao IS 'Classificacao da situação em relação as brancas:
1 - Boa
2 - Ruim
3 - Empate';


--
-- TOC entry 2146 (class 0 OID 0)
-- Dependencies: 174
-- Name: COLUMN heu_situacoesdejogo.etapa_geral; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN heu_situacoesdejogo.etapa_geral IS '0 - Inicio de jogo
1 - Meio de jogo
2 - Finalização';


--
-- TOC entry 175 (class 1259 OID 16472)
-- Name: heu_tipo; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE heu_tipo (
    cod_tipo bigint NOT NULL,
    cod_classe bigint NOT NULL,
    txt_nome character varying NOT NULL,
    txt_descricao character varying NOT NULL,
    CONSTRAINT ckc_txt_nome_heu_tipo CHECK (((txt_nome)::text = upper((txt_nome)::text)))
);


ALTER TABLE public.heu_tipo OWNER TO postgres;

--
-- TOC entry 176 (class 1259 OID 16479)
-- Name: heu_turma; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE heu_turma (
    cod_turma bigint NOT NULL,
    txt_nome character varying NOT NULL,
    txt_descricao character varying,
    cod_instituicao bigint NOT NULL,
    bit_permissoes smallint DEFAULT 0 NOT NULL,
    cod_situacao bigint NOT NULL,
    dat_criacao timestamp with time zone DEFAULT now() NOT NULL,
    dat_cancelamento timestamp with time zone,
    CONSTRAINT chc_bit_permissoes_turma CHECK ((bit_permissoes >= 0)),
    CONSTRAINT chc_txt_nome_heu_turma CHECK (((txt_nome)::text = upper((txt_nome)::text)))
);


ALTER TABLE public.heu_turma OWNER TO postgres;

--
-- TOC entry 177 (class 1259 OID 16489)
-- Name: heu_turma_usuario; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE heu_turma_usuario (
    cod_turma bigint NOT NULL,
    cod_usuario bigint NOT NULL,
    cod_tipo bigint NOT NULL,
    dat_criacao timestamp with time zone DEFAULT now() NOT NULL,
    dat_cancelamento timestamp with time zone
);


ALTER TABLE public.heu_turma_usuario OWNER TO postgres;

--
-- TOC entry 178 (class 1259 OID 16493)
-- Name: heu_usuario; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE heu_usuario (
    cod_usuario bigint NOT NULL,
    cod_tipo bigint NOT NULL,
    txt_login character varying NOT NULL,
    txt_senha character varying(32) NOT NULL,
    txt_nome character varying NOT NULL,
    txt_foto character varying,
    log_sexo_masculino boolean NOT NULL,
    dat_nascimento date NOT NULL,
    txt_email character varying NOT NULL,
    dat_criacao timestamp without time zone DEFAULT now() NOT NULL,
    log_online boolean DEFAULT false NOT NULL,
    qtd_anotacoes_para_outros bigint DEFAULT 0 NOT NULL,
    qtd_acessos_realizados bigint DEFAULT 0 NOT NULL,
    qtd_copias_realizadas bigint DEFAULT 0 NOT NULL,
    bit_permissoes bigint DEFAULT 0 NOT NULL,
    dat_cancelamento timestamp without time zone,
    cod_situacao bigint NOT NULL,
    CONSTRAINT chc_bit_permissoes_heu_usuario CHECK ((bit_permissoes >= 0)),
    CONSTRAINT chc_txt_email_heu_usuario CHECK (((txt_email)::text = lower((txt_email)::text))),
    CONSTRAINT chc_txt_senha_usuario CHECK (((txt_senha)::text = lower((txt_senha)::text))),
    CONSTRAINT ckc_qtd_acessos_reali_heu_usua CHECK ((qtd_acessos_realizados >= 0)),
    CONSTRAINT ckc_qtd_anotacoes_par_heu_usua CHECK ((qtd_anotacoes_para_outros >= 0)),
    CONSTRAINT ckc_qtd_copias_realiz_heu_usua CHECK ((qtd_copias_realizadas >= 0)),
    CONSTRAINT ckc_txt_login_heu_usua CHECK (((txt_login)::text = lower((txt_login)::text))),
    CONSTRAINT ckc_txt_nome_heu_usua CHECK (((txt_nome)::text = upper((txt_nome)::text)))
);


ALTER TABLE public.heu_usuario OWNER TO postgres;

--
-- TOC entry 179 (class 1259 OID 16513)
-- Name: seq_heu_avaliacao; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_heu_avaliacao
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.seq_heu_avaliacao OWNER TO postgres;

--
-- TOC entry 2152 (class 0 OID 0)
-- Dependencies: 179
-- Name: seq_heu_avaliacao; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('seq_heu_avaliacao', 55137, true);


--
-- TOC entry 180 (class 1259 OID 16515)
-- Name: seq_heu_componente; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_heu_componente
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
    CYCLE;


ALTER TABLE public.seq_heu_componente OWNER TO postgres;

--
-- TOC entry 2154 (class 0 OID 0)
-- Dependencies: 180
-- Name: seq_heu_componente; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('seq_heu_componente', 1061, true);


--
-- TOC entry 184 (class 1259 OID 16771)
-- Name: seq_heu_historico; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_heu_historico
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.seq_heu_historico OWNER TO postgres;

--
-- TOC entry 2156 (class 0 OID 0)
-- Dependencies: 184
-- Name: seq_heu_historico; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE seq_heu_historico OWNED BY heu_historico.cod_historico;


--
-- TOC entry 2157 (class 0 OID 0)
-- Dependencies: 184
-- Name: seq_heu_historico; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('seq_heu_historico', 2694, true);


--
-- TOC entry 181 (class 1259 OID 16517)
-- Name: seq_heu_situacaodejogo; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_heu_situacaodejogo
    START WITH 7029
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.seq_heu_situacaodejogo OWNER TO postgres;

--
-- TOC entry 2159 (class 0 OID 0)
-- Dependencies: 181
-- Name: seq_heu_situacaodejogo; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('seq_heu_situacaodejogo', 7029, true);


--
-- TOC entry 182 (class 1259 OID 16519)
-- Name: seq_heu_usuario; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_heu_usuario
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
    CYCLE;


ALTER TABLE public.seq_heu_usuario OWNER TO postgres;

--
-- TOC entry 2161 (class 0 OID 0)
-- Dependencies: 182
-- Name: seq_heu_usuario; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('seq_heu_usuario', 71, true);


--
-- TOC entry 2036 (class 2604 OID 16773)
-- Name: cod_historico; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY heu_historico ALTER COLUMN cod_historico SET DEFAULT nextval('seq_heu_historico'::regclass);


--
-- TOC entry 2117 (class 0 OID 16397)
-- Dependencies: 168
-- Data for Name: heu_avaliacoes; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY heu_avaliacoes (cod_avaliacao, cod_usuario, cod_situacao, cod_tipo, cod_componente, aplicavel, condicao_regra, valor_tabuleiro, data_modificacao, data_criacao, comando_aplicavel, valor_peao, valor_torre, valor_cavalo, valor_bispo, valor_dama, cod_conjunto, valor_brancas, valor_pretas) FROM stdin;
\.


--
-- TOC entry 2118 (class 0 OID 16412)
-- Dependencies: 169
-- Data for Name: heu_classe; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY heu_classe (cod_classe, txt_nome, txt_descricao) FROM stdin;
4	HEURÍSTICA	Tipos de Heurísticas disponíveis
5	ETAPA	Tipos de Etapas Disponíveis
6	REGIÃO	Tipos de Regiões Disponíveis
3	ANOTAÇÃO	Tipos de Anotações disponíveis
1	USUÁRIO	Tipos de usuários existentes no sistema
2	CONJUNTO HEURÍSTICO	Níveis de Complexidade do Conjunto Heurístico
7	EXPRESSÃO DE CÁLCULO HEURÍSTICO	Tipos de Expressões de Cálculo Heurísticos
8	FUNÇÃO	Tipos de Funções
9	SITUAÇÃO DE JOGO	Tipos de Situações de Jogo
10	SITUAÇÃO DO USUÁRIO	Situações do cadastro do Usuário
11	SITUAÇÃO DA TURMA	Situações da Turma
12	HISTÓRICO	Tipos de históricos armazenados pelo sistema
\.


--
-- TOC entry 2119 (class 0 OID 16419)
-- Dependencies: 170
-- Data for Name: heu_componente; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY heu_componente (cod_componente, cod_usuario, cod_tipo, txt_nome, txt_definicao, num_versao, dat_criacao, dat_ultima_modificacao, qtd_acessos, qtd_copias, qtd_anotacoes_recebidas, bit_permissoes) FROM stdin;
2	1	26	ONDE_ESTAO_PECAS(TIPO_PECA[],JOGADOR) RETORNA CASA[]	FUNCAO ONDE_ESTAO_PECAS\n  DESCRICAO "Retorna um conjunto de casas onde as pecas dos tipos definidos e pertencentes ao jogador estão localizadas"\n  RETORNA\n     CASA[] DESCRICAO "Conjunto de casas onde as pecas estão localizadas"\n  PARAMETROS\n     TIPO_PECA[] TIPOS DESCRICAO "Tipos das Pecas que devem ser localizadas" \n     JOGADOR     JOG   DESCRICAO "Jogador que terá as peças localizadas"\n  COMANDOS\n     INTEIRO INDICE1\n     CASA[]  RESPOSTA\n     PECA    ATUAL\n     PARA INDICE1 DE 1 ATE TABULEIRO.PECAS.TOTAL() FACA\n        ATUAL <- TABULEIRO.PECAS.ELEMENTO(INDICE1)\n        SE (ATUAL.TIPO PERTENCE TIPOS) E (ATUAL.DONO IGUAL JOG) ENTAO\n           RESPOSTA.ADICIONAR(ATUAL.CASA)\n        FIM SE\n     FIM PARA\n     RETORNA RESPOSTA\nFIM FUNCAO	1	2006-09-14 00:09:00.484	2006-09-14 00:09:00.484	0	0	0	0
3	1	26	QUAIS_PECAS_ESTAO(CASA[],JOGADOR) RETORNA TIPO_PECA[]	FUNCAO QUAIS_PECAS_ESTAO\n  DESCRICAO "Retorna um conjunto dos tipos de peças que estão dentro da região passada e pertecem ao jogador"\n  RETORNA\n     TIPO_PECA[] DESCRICAO "Conjunto de tipo de peças que estão localizadas na região passada"\n  PARAMETROS\n     CASA[]  REGIAO DESCRICAO "Região onde será realizada a procura por peças" \n     JOGADOR JOG    DESCRICAO "Jogador que terá as peças identificadas dentro da região"\n  COMANDOS\n     INTEIRO     INDICE1\n     TIPO_PECA[] RESPOSTA\n     CASA        ATUAL\n     PECA        PECA1\n     PARA INDICE1 DE 1 ATE REGIAO.TOTAL() FACA\n        ATUAL <- REGIAO.ELEMENTO(INDICE1)\n        PECA1 <- ATUAL.PECA_ATUAL\n        SE (PECA1 DIFERENTE VAZIO) E (PECA1.DONO IGUAL JOG) ENTAO\n           RESPOSTA.ADICIONAR(PECA1.TIPO)\n        FIM SE\n     FIM PARA\n     RETORNA RESPOSTA\nFIM FUNCAO	1	2006-09-14 00:09:27.703	2006-09-14 00:09:27.703	0	0	0	0
4	1	26	PRESENCA_PECAS(TIPO_PECA[],CASA[],JOGADOR) RETORNA LOGICO	FUNCAO PRESENCA_PECAS\n  DESCRICAO "Retorna verdadeiro caso existam peças dos tipos passados pertencentes ao jogador na região especificada"\n  RETORNA\n     LOGICO DESCRICAO "Retorna Verdadeiro caso exista pelo menos uma peça de cada tipo passado dentro da região"\n  PARAMETROS\n     TIPO_PECA[] TIPOS  DESCRICAO "Tipos de Peças que serão procurados"\n     CASA[]      REGIAO DESCRICAO "Região onde será realizada a procura por peças" \n     JOGADOR     JOG    DESCRICAO "Jogador que terá as peças procuradas"\n  COMANDOS\n     INTEIRO INDICE1\n     CASA    ATUAL\n     PECA    PECA1\n     PARA INDICE1 DE 1 ATE REGIAO.TOTAL() FACA\n        ATUAL <- REGIAO.ELEMENTO(INDICE1)\n        PECA1 <- ATUAL.PECA_ATUAL\n        SE (PECA1 PERTENCE TIPOS) E (PECA1.DONO IGUAL JOG) ENTAO\n           TIPOS.REMOVER(PECA1.TIPO)\n        FIM SE\n     FIM PARA\n     SE (TIPOS.TOTAL() IGUAL 0) ENTAO\n        RETORNA VERDADEIRO\n      SENAO\n        RETORNA FALSO\n     FIM SE\nFIM FUNCAO	1	2006-09-14 00:09:52.328	2006-09-14 00:09:52.328	0	0	0	0
5	1	27	QUANTIDADE_PECAS(TIPO_PECA[],CASA[],JOGADOR) RETORNA INTEIRO	FUNCAO QUANTIDADE_PECAS\n  DESCRICAO "Retorna a quantidade de peças que existem na região, são dos tipos especificados, e que pertencem ao jogador"\n  RETORNA\n     INTEIRO DESCRICAO "Quantidade de peças dos tipos especificados do jogador que estão na região"\n  PARAMETROS\n     TIPO_PECA[] TIPOS  DESCRICAO "Tipos de Peças que serão contadas"\n     CASA[]      REGIAO DESCRICAO "Região onde será realizada a contagem de peças" \n     JOGADOR     JOG    DESCRICAO "Jogador que deverá ter as peças contadas"\n  COMANDOS\n     INTEIRO INDICE1\n     INTEIRO RESPOSTA\n     CASA    ATUAL\n     PECA    PECA1\n     PARA INDICE1 DE 1 ATE REGIAO.TOTAL() FACA\n        ATUAL <- REGIAO.ELEMENTO(INDICE1)\n        PECA1 <- ATUAL.PECA_ATUAL\n        SE (PECA1 PERTENCE TIPOS) E (PECA1.DONO IGUAL JOG) ENTAO\n           RESPOSTA <- RESPOSTA + 1\n        FIM SE\n     FIM PARA\n     RETORNA RESPOSTA\nFIM FUNCAO	1	2006-09-14 00:10:23.437	2006-09-14 00:10:23.437	0	0	0	0
6	1	27	MAIOR_QUANTIDADE_PECAS(CASA[],JOGADOR) RETORNA LOGICO	FUNCAO MAIOR_QUANTIDADE_PECAS\n  DESCRICAO "Retorna verdadeiro caso o jogador tenha a maior quantidade de peças na região especificada"\n  RETORNA\n     LOGICO DESCRICAO "Retorna verdadeiro caso o jogador possua mais peças que o oponente na região"\n  PARAMETROS\n     CASA[]  REGIAO DESCRICAO "Região onde será realizada a contagem de peças dos jogadores" \n     JOGADOR JOG    DESCRICAO "Jogador que será testado se possui a maior quantidade de peças"\n  COMANDOS\n     INTEIRO INDICE1\n     INTEIRO QUANTIDADE_JOG1\n     INTEIRO QUANTIDADE_JOG2\n     CASA    ATUAL\n     PECA    PECA1\n     PARA INDICE1 DE 1 ATE REGIAO.TOTAL() FACA\n        ATUAL <- REGIAO.ELEMENTO(INDICE1)\n        PECA1 <- ATUAL.PECA_ATUAL\n        SE (PECA1.DONO IGUAL JOG) ENTAO\n           QUANTIDADE_JOG1 <- QUANTIDADE_JOG1 + 1\n         SENAO\n           QUANTIDADE_JOG2 <- QUANTIDADE_JOG2 + 1\n        FIM SE\n     FIM PARA\n     SE (QUANTIDADE_JOG1 > QUANTIDADE_JOG2) ENTAO\n        RETORNA VERDADEIRO\n      SENAO\n        RETORNA FALSO\n     FIM SE\nFIM FUNCAO	1	2006-09-14 00:10:45	2006-09-14 00:10:45	0	0	0	0
8	1	28	MAIOR_VALOR_PECAS(CASA[],JOGADOR) RETORNA LOGICO	FUNCAO MAIOR_VALOR_PECAS\n  DESCRICAO "Retorna verdadeiro caso o jogador tenha a maior soma de valores de peças na região especificada"\n  RETORNA\n     LOGICO DESCRICAO "Retorna verdadeiro caso o jogador possua uma soma de peças maior que o oponente na região"\n  PARAMETROS\n     CASA[]  REGIAO DESCRICAO "Região onde será realizada a soma das peças dos jogadores" \n     JOGADOR JOG    DESCRICAO "Jogador que será testado se possui a maior soma de valores de peças"\n  COMANDOS\n     INTEIRO INDICE1\n     INTEIRO SOMA_JOG1\n     INTEIRO SOMA_JOG2\n     CASA    ATUAL\n     PECA    PECA1\n     PARA INDICE1 DE 1 ATE REGIAO.TOTAL() FACA\n        ATUAL <- REGIAO.ELEMENTO(INDICE1)\n        PECA1 <- ATUAL.PECA_ATUAL\n        SE (PECA1.DONO IGUAL JOG) ENTAO\n           SOMA_JOG1 <- SOMA_JOG1 + 1\n         SENAO\n           SOMA_JOG2 <- SOMA_JOG2 + 1\n        FIM SE\n     FIM PARA\n     SE (SOMA_JOG1 > SOMA_JOG2) ENTAO\n        RETORNA VERDADEIRO\n      SENAO\n        RETORNA FALSO\n     FIM SE\nFIM FUNCAO	1	2006-09-14 00:11:45.531	2006-09-14 00:11:45.531	0	0	0	0
13	1	29	QUEM_PROTEGE(TIPO_PECA[],CASA[],JOGADOR) RETORNA TIPO_PECA[]	FUNCAO QUEM_PROTEGE\n  DESCRICAO "Retorna os tipos de peças do jogador que estão protegendo as peças dos tipos passados, dentro da região"\n  RETORNA\n     TIPO_PECA[] DESCRICAO "Conjunto de tipos de peças do jogaodr que estão protegendo as peças dos tipos passados"\n  PARAMETROS\n     TIPO_PECA[] TIPOS  DESCRICAO "Tipos de peças que deverão ser verificadas quem protege elas"\n     CASA[]      REGIAO DESCRICAO "Região onde será verificada a proteção das peças"\n     JOGADOR     JOG    DESCRICAO "Jogador que terá as peças verificadas"\n  COMANDOS\n     # FALTA IMPLEMENTAR \nFIM FUNCAO	1	2006-09-14 00:14:28	2006-09-14 00:14:28	0	0	0	0
14	1	29	QUEM_ESTA_PROTEGIDO(TIPO_PECA[],CASA[],JOGADOR) RETORNA TIPO_PECA[]	FUNCAO QUEM_ESTA_PROTEGIDO\n  DESCRICAO "Retorna os tipos de peças do jogador que estão sendo protegidos pelas peças dos tipos passados, na região"\n  RETORNA\n     TIPO_PECA[] DESCRICAO "Conjunto de tipos de peças do jogador que estão sendo protegidas pelas peças dos tipos passados"\n  PARAMETROS\n     TIPO_PECA[] TIPOS  DESCRICAO "Tipos de peças que deverão ser verificadas quem elas estão protegendo"\n     CASA[]      REGIAO DESCRICAO "Região onde será verificada a proteção das peças"\n     JOGADOR     JOG    DESCRICAO "Jogador que terá as peças verificadas"\n  COMANDOS\n     # FALTA IMPLEMENTAR \nFIM FUNCAO	1	2006-09-14 00:14:45.453	2006-09-14 00:14:45.453	0	0	0	0
16	1	29	QUEM_ESTA_AMEACADO(TIPO_PECA[],CASA[],JOGADOR) RETORNA TIPO_PECA[]	FUNCAO QUEM_ESTA_AMEACADO\n  DESCRICAO "Retorna os tipos de peças do jogador que estão sendo ameaçadas pelas peças dos tipos passados, dentro da região"\n  RETORNA\n     TIPO_PECA[] DESCRICAO "Conjunto de tipos de peças do jogador que estão sendo ameaçadas pelas peças dos tipos passados"\n  PARAMETROS\n     TIPO_PECA[] TIPOS  DESCRICAO "Tipos de peças que deverão ser verificadas quem elas estão ameaçando"\n     CASA[]      REGIAO DESCRICAO "Região onde será verificada a ameaça das peças"\n     JOGADOR     JOG    DESCRICAO "Jogador que terá as peças verificadas"\n  COMANDOS\n     # FALTA IMPLEMENTAR \nFIM FUNCAO	1	2006-09-14 00:15:22.781	2006-09-14 00:15:22.781	0	0	0	0
17	1	29	POSSIVEL_ROQUE_PEQUENO(JOGADOR) RETORNA LOGICO	FUNCAO POSSIVEL_ROQUE_PEQUENO\n  DESCRICAO "Verifica se é possível para o Jogador passado executar a jogada Roque Pequeno"\n  RETORNA\n     LOGICO DESCRICAO "Retorna verdadeiro se ainda for possível que o jogador passado executar a jogada Roque Pequeno"\n  PARAMETROS\n     JOGADOR JOG DESCRICAO "Jogador que deverá ser verificado a possibilidade de Roque Pequeno"\n  COMANDOS\n     # FALTA IMPLEMENTAR \nFIM FUNCAO	1	2006-09-14 00:15:40.843	2006-09-14 00:15:40.843	0	0	0	0
18	1	29	POSSIVEL_ROQUE_GRANDE(JOGADOR) RETORNA LOGICO	FUNCAO POSSIVEL_ROQUE_GRANDE\n  DESCRICAO "Verifica se é possível para o Jogador passado executar a jogada Roque Grande"\n  RETORNA\n     LOGICO DESCRICAO "Retorna verdadeiro se ainda for possível que o jogador passado executar a jogada Roque Grande"\n  PARAMETROS\n     JOGADOR JOG DESCRICAO "Jogador que deverá ser verificado a possibilidade de Roque Grande"\n  COMANDOS\n     # FALTA IMPLEMENTAR \nFIM FUNCAO	1	2006-09-14 00:16:00.765	2006-09-14 00:16:00.765	0	0	0	0
11	1	29	ESTA_AMEACANDO(TIPO_PECA[],CASA[],JOGADOR) RETORNO LOGICO	FUNCAO ESTA_AMEACANDO\n  DESCRICAO "Retorna verdadeiro caso alguma peça do jogador dos tipos passados esteja ameaçando outra na região"\n  RETORNA\n     LOGICO DESCRICAO "Retorna verdadeiro caso alguma peça do jogador dos tipos passados esteja ameaçando outra na região"\n  PARAMETROS\n     TIPO_PECA[] TIPOS  DESCRICAO "Tipos de peças que deverão ser verificadas"\n     CASA[]      REGIAO DESCRICAO "Região onde será verificada a ameaça das peças"\n     JOGADOR     JOG    DESCRICAO "Jogador que terá as peças verificadas"\n  COMANDOS\n     # FALTA IMPLEMENTAR \nFIM FUNCAO	1	2006-09-14 00:13:51.875	2006-09-14 00:13:51.875	0	0	0	0
10	1	29	ESTA_PROTEGENDO(TIPO_PECA[],CASA[],JOGADOR) RETORNA LOGICO	FUNCAO ESTA_PROTEGENDO\n  DESCRICAO "Retorna verdadeiro caso alguma peça do jogador dos tipos de peças passados esteja protegendo outra na região"\n  RETORNA\n     LOGICO DESCRICAO "Retorna verdadeiro caso alguma peça do jogador dos tipos de peças passados esteja protegendo outra na região"\n  PARAMETROS\n     TIPO_PECA[] TIPOS  DESCRICAO "Tipos de peças que deverão ser verificadas"\n     CASA[]      REGIAO DESCRICAO "Região onde será verificada a proteção das peças"\n     JOGADOR     JOG    DESCRICAO "Jogador que terá as peças verificadas"\n  COMANDOS\n     # FALTA IMPLEMENTAR \nFIM FUNCAO	1	2006-09-14 00:13:25.437	2006-09-14 00:13:25.437	0	0	0	0
19	1	29	POSSUI_PECA_EN_PASSANT(JOGADOR) RETORNA LOGICO	FUNCAO POSSUI_PECA_EN_PASSANT\r\n  DESCRICAO "Retorna verdadeiro caso o jogador possua um Peão passível de ser capturado En Passant"\r\n  RETORNA\r\n     LOGICO DESCRICAO "Retorna verdadeiro caso o jogador possua um Peão passível de ser capturado En Passant"\r\n  PARAMETROS\r\n     JOGADOR JOG DESCRICAO "Jogador que deverá ser verificado a possibilidade de captura En Passant"\r\n  COMANDOS\r\n     # FALTA IMPLEMENTAR \r\nFIM FUNCAO	1	2006-09-14 00:16:19.843	2006-09-14 00:16:19.843	0	0	0	0
15	1	29	QUEM_AMEACA(TIPO_PECA[],CASA[],JOGADOR) RETORNA TIPO_PECA[]	FUNCAO QUEM_AMEACA\n  DESCRICAO "Retorna os tipos de peças do jogador que estão ameaçando as peças dos tipos especificados, dentro da região"\n  RETORNA\n     TIPO_PECA[] DESCRICAO "Conjunto de tipos de peças do jogador que estão ameaçando as peças dos tipos passados, na região"\n  PARAMETROS\n     TIPO_PECA[] TIPOS  DESCRICAO "Tipos de peças que deverão ser verificadas quem está ameaçando elas"\n     CASA[]      REGIAO DESCRICAO "Região onde será verificada a ameaça das peças"\n     JOGADOR     JOG    DESCRICAO "Jogador que terá as peças verificadas"\n  COMANDOS\n     # FALTA IMPLEMENTAR \nFIM FUNCAO	1	2006-09-14 00:15:05.046	2006-09-14 00:15:05.046	0	0	0	0
12	1	29	ESTA_AMEACADA(TIPO_PECA[],CASA[],JOGADOR) RETORNO LOGICO	FUNCAO ESTA_AMEACADA\n  DESCRICAO "Retorna verdadeiro caso alguma peça do jogador dos tipos passados esteja ameaçada na região"\n  RETORNA\n     LOGICO DESCRICAO "Retorna verdadeiro caso alguma peça do jogador dos tipos passados esteja ameaçada na região"\n  PARAMETROS\n     TIPO_PECA[] TIPOS  DESCRICAO "Tipos de peças que deverão ser verificadas"\n     CASA[]      REGIAO DESCRICAO "Região onde será verificada a ameaça das peças"\n     JOGADOR     JOG    DESCRICAO "Jogador que terá as peças verificadas"\n  COMANDOS\n     # FALTA IMPLEMENTAR \nFIM FUNCAO	1	2006-09-14 00:14:09.609	2006-09-14 00:14:09.609	0	0	0	0
9	1	29	ESTA_PROTEGIDA(TIPO_PECA[],CASA[],JOGADOR) RETORNA LOGICO	FUNCAO ESTA_PROTEGIDA\n  DESCRICAO "Retorna verdadeiro caso todas as peças do jogador dos tipos especificados estejam protegidas se elas existirem dentro da região passada"\n  RETORNA\n     LOGICO DESCRICAO "Retorna verdadeiro caso todas as peças do jogador dos tipos especificados estejam protegidas se elas existirem dentro da região passada"\n  PARAMETROS\n     TIPO_PECA[] TIPOS  DESCRICAO "Tipos de peças que deverão ser verificadas"\n     CASA[]      REGIAO DESCRICAO "Região onde será verificada a proteção das peças"\n     JOGADOR     JOG    DESCRICAO "Jogador que terá as peças verificadas"\n  COMANDOS\n     # FALTA IMPLEMENTAR \nFIM FUNCAO	1	2006-09-14 00:13:07.796	2006-09-14 00:13:07.796	0	0	0	0
1	1	25	QUANTIDADE_JOGADAS() RETORNA INTEIRO	FUNCAO QUANTIDADE_JOGADAS\r\n  DESCRICAO "Retorna a quantidade de jogadas realizadas ate o momento na partida"\r\n  RETORNA\r\n     INTEIRO DESCRICAO "Quantidade de jogadas"\r\n  COMANDOS\r\n     RETORNA TABULEIRO.QUANTIDADE_JOGADAS\r\nFIM FUNCAO	1	2006-09-14 00:08:16.921	2006-09-14 00:08:16.921	0	0	0	0
591	1	29	PECAS_AMEACADAS_POR(TIPO_PECA[],CASA[],JOGADOR) RETORNA PECA[]	FUNCAO PECAS_AMEACADAS_POR\r\n  DESCRICAO "Retorna as peças do jogador que estão sendo ameaçadas pelas peças dos tipos passados, dentro da região"\r\n  RETORNA\r\n     PECA[] DESCRICAO "Conjunto de peças do jogador que estão sendo ameaçadas pelas peças dos tipos passados"\r\n  PARAMETROS\r\n     TIPO_PECA[] TIPOS  DESCRICAO "Tipos de peças que deverão ser verificadas quem elas estão ameaçando"\r\n     CASA[]      REGIAO DESCRICAO "Região onde será verificada a ameaça das peças"\r\n     JOGADOR     JOG    DESCRICAO "Jogador que terá as peças verificadas"\r\n   COMANDOS\r\n      # FALTA IMPLEMENTAR \r\nFIM FUNCAO	1	2012-07-25 16:28:01.687	2012-07-25 16:28:01.687	0	0	0	0
590	1	29	PECAS_QUE_AMEACAM(TIPO_PECA[],CASA[],JOGADOR) RETORNA PECA[]	FUNCAO PECAS_QUE_AMEACAM\r\n  DESCRICAO "Retorna a peças do jogador que estão ameaçando as peças dos tipos especificados, dentro da região"\r\n  RETORNA\r\n    PECA[] DESCRICAO "Conjunto de peças do jogador que estão ameaçando as peças dos tipos passados, na região"\r\n  PARAMETROS\r\n    TIPO_PECA[] TIPOS  DESCRICAO "Tipos de peças que deverão ser verificadas quem está ameaçando elas"\r\n    CASA[]      REGIAO DESCRICAO "Região onde será verificada a ameaça das peças"\r\n    JOGADOR     JOG    DESCRICAO "Jogador que terá as peças verificadas"\r\n   COMANDOS\r\n     # FALTA IMPLEMENTAR \r\nFIM FUNCAO	1	2012-07-25 16:02:31.64	2012-07-25 16:02:31.64	0	0	0	0
7	1	28	SOMA_PECAS(TIPO_PECA[],CASA[],JOGADOR) RETORNA REAL	FUNCAO SOMA_PECAS\n  DESCRICAO "Retorna a soma da peças que existem na região, são dos tipos especificados, e que pertencem ao jogador"\n  RETORNA\n     REAL DESCRICAO "Soma das peças dos tipos especificados do jogador que estão na região"\n  PARAMETROS\n     TIPO_PECA[] TIPOS  DESCRICAO "Tipos de Peças que serão somadas"\n     CASA[]      REGIAO DESCRICAO "Região onde será realizada a procurar pelas peças" \n     JOGADOR     JOG    DESCRICAO "Jogador que deverá ter as peças somadas"\n  COMANDOS\n     INTEIRO INDICE1\n     REAL    RESPOSTA\n     CASA    ATUAL\n     PECA    PECA1\n     PARA INDICE1 DE 1 ATE REGIAO.TOTAL() FACA\n        ATUAL <- REGIAO.ELEMENTO(INDICE1)\n        PECA1 <- ATUAL.PECA_ATUAL\n        SE (PECA1 PERTENCE TIPOS) E (PECA1.DONO IGUAL JOG) ENTAO\n           RESPOSTA <- RESPOSTA + PECA1.TIPO.VALOR\n        FIM SE\n     FIM PARA\n     RETORNA RESPOSTA\nFIM FUNCAO	1	2006-09-14 00:11:23.921	2006-09-14 00:11:23.921	0	0	0	0
585	1	26	PECAS_QUE_ESTAO(TIPO_PECA[],CASA[],JOGADOR) RETORNA PECA[]	FUNCAO PECAS_QUE_ESTAO\r\n  DESCRICAO "Retorna um conjunto com as peças que estão dentro da região passada, que são dos tipos procurados, e pertecem ao jogador"\r\n  RETORNA\r\n    PECA[] DESCRICAO "Conjunto de peças que estão localizadas na região passada"\r\n  PARAMETROS\r\n    TIPO_PECA[] TIPOS  DESCRICAO "Tipos das Pecas que devem ser localizadas" \r\n     CASA[]      REGIAO DESCRICAO "Região onde será realizada a procura por peças" \r\n     JOGADOR     JOG    DESCRICAO "Jogador que terá as peças identificadas dentro da região"\r\n  COMANDOS\r\n     INTEIRO INDICE1\r\n     PECA[]  RESPOSTA\r\n     CASA    ATUAL\r\n     PECA    PECA1\r\n     PARA INDICE1 DE 1 ATE REGIAO.TOTAL() FACA\r\n        ATUAL <- REGIAO.ELEMENTO(INDICE1)\r\n        PECA1 <- ATUAL.PECA_ATUAL\r\n        SE (PECA1 DIFERENTE VAZIO) E (PECA1.DONO IGUAL JOG) E \r\n             (PECA1.TIPO PERTENCE TIPOS) ENTAO\n             RESPOSTA.ADICIONAR(PECA1)\r\n        FIM SE\r\n     FIM PARA\r\n     RETORNA RESPOSTA\r\nFIM FUNCAO	1	2012-07-24 19:49:48.812	2012-07-24 19:49:48.812	0	0	0	0
554	1	25	LANCES_SEM_CAPTURA_E_PEAO() RETORNA INTEIRO	FUNCAO LANCES_SEM_CAPTURA_E_PEAO\r\n  DESCRICAO "Retorna a quantidade de lances realizados sem movimento de Peões e sem capturas de peças"\r\n  RETORNA\r\n    INTEIRO DESCRICAO "Quantidade de lances sem captura e movimento de peões"\r\n  COMANDOS\r\n    RETORNA TABULEIRO.LANCES_SEM_CAPTURA_E_MOVIMENTO_PEAO\r\nFIM FUNCAO	11	2012-07-20 13:11:18.531	2012-10-18 16:10:30.812	0	0	0	0
\.


--
-- TOC entry 2120 (class 0 OID 16438)
-- Dependencies: 171
-- Data for Name: heu_componente_componente; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY heu_componente_componente (cod_componente_principal, cod_componente_incluido, dat_inclusao) FROM stdin;
\.


--
-- TOC entry 2121 (class 0 OID 16442)
-- Dependencies: 172
-- Data for Name: heu_desempenho_conj_heu; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY heu_desempenho_conj_heu (cod_componente, qtd_competicoes_disputadas, qtd_partidas_ganhas, qtd_partidas_perdidas, qtd_partidas_empatadas) FROM stdin;
\.


--
-- TOC entry 2128 (class 0 OID 16694)
-- Dependencies: 183
-- Data for Name: heu_historico; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY heu_historico (cod_historico, cod_usuario, cod_tipo, txt_descricao, cod_componente, cod_tipo_componente, cod_autor_componente, dat_criacao) FROM stdin;
\.


--
-- TOC entry 2122 (class 0 OID 16453)
-- Dependencies: 173
-- Data for Name: heu_instituicao; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY heu_instituicao (cod_instituicao, txt_nome, dat_criacao, dat_cancelamento, txt_descricao, cod_usuario) FROM stdin;
\.


--
-- TOC entry 2123 (class 0 OID 16461)
-- Dependencies: 174
-- Data for Name: heu_situacoesdejogo; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY heu_situacoesdejogo (cod_situacao, cod_usuario, fen, data_criacao, data_modificacao, classificacao, txt_definicao, etapa_geral) FROM stdin;
6935	1	rn1qkbnr/ppp1pppp/8/8/2Pp2b1/8/PP1PPPBP/RNBQK1NR w KQkq - 0 1	2008-02-17	2008-02-17	6	<coloque sua definição aqui>	0
6939	1	r1bqkbnr/pp1ppppp/2n5/2p5/8/2NP2P1/PPP1PP1P/R1BQKBNR b KQkq - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	0
6941	1	rnbqkbnr/p1pppppp/1p6/8/8/1P6/P1PPPPPP/RNBQKBNR w KQkq - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	0
6951	1	rnbqkbnr/ppp1pppp/8/8/2p5/5N2/PP1PPPPP/RNBQKB1R w KQkq - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	0
6952	1	rn1qkbnr/ppp1pppp/4b3/8/2p5/4PN2/PP1P1PPP/RNBQKB1R w KQkq - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	0
6953	1	rnbqkbnr/ppp1pppp/8/8/2p5/5N2/PP1PPPPP/RNBQKB1R w KQkq - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	0
6954	1	rn1qkbnr/ppp1pppp/4b3/8/2p5/4PN2/PP1P1PPP/RNBQKB1R w KQkq - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	0
6966	1	r3k2r/1pq1bpp1/p1bp1n1p/4p3/3QPPPP/2N1B3/PPP1B3/2KR3R w kq - 0 1	2008-02-17	2008-02-17	4	<coloque sua definição aqui>	1
6967	1	r3k2r/1pq1bpp1/p1bp1n1p/8/4PpPP/2NQB3/PPP1B3/2KR3R w kq - 0 1	2008-02-17	2008-02-17	4	<coloque sua definição aqui>	1
6968	1	2kr3r/1pq1bpp1/p1bp1n1p/8/4PBPP/2NQ4/PPP1B3/2KR3R w - - 0 1	2008-02-17	2008-02-17	4	<coloque sua definição aqui>	1
6969	1	2kr3r/1pq1bpp1/p1bp1n2/6p1/4PB1P/2NQ4/PPP1B3/2KR3R w - - 0 1	2008-02-17	2008-02-17	4	<coloque sua definição aqui>	1
6970	1	2kr3r/1pqnbpp1/p1bp4/6P1/4PB2/2NQ4/PPP1B3/2KR3R w - - 0 1	2008-02-17	2008-02-17	4	<coloque sua definição aqui>	1
6971	1	2k4r/1pqnbpp1/p1bp4/6P1/4PB2/2NQ4/PPP1B3/2KR4 w - - 0 1	2008-02-17	2008-02-17	4	<coloque sua definição aqui>	1
6972	1	2k4r/1pqn1pp1/p1bb4/6P1/4P3/2NQ4/PPP1B3/2KR4 w - - 0 1	2008-02-17	2008-02-17	4	<coloque sua definição aqui>	1
6973	1	2k4r/1p1n1pp1/p1bq4/6P1/4P3/2N5/PPP1B3/2KR4 w - - 0 1	2008-02-17	2008-02-17	4	<coloque sua definição aqui>	1
6974	1	2k5/1p1n1pp1/p1bR4/6P1/4P3/2N5/PPP1B3/2K4r w - - 0 1	2008-02-17	2008-02-17	4	<coloque sua definição aqui>	1
6975	1	1r1n2k1/r2nb1pp/2p1pp2/p1PpP3/P2B4/R4N2/1PR2PPP/2N2K2 w - - 0 1	2008-02-17	2008-02-17	4	<coloque sua definição aqui>	1
6976	1	1r1n2k1/1r1nb1pp/2p1pp2/p1PpP3/P2B4/1R3N2/1PR2PPP/2N2K2 w - - 0 1	2008-02-17	2008-02-17	4	<coloque sua definição aqui>	1
6977	1	3n2k1/1r1nb1pp/2p1pp2/p1PpP3/P2B4/5N2/1PR2PPP/2N2K2 w - - 0 1	2008-02-17	2008-02-17	4	<coloque sua definição aqui>	1
6978	1	3n2k1/r2nb1pp/2p1pp2/p1PpP3/P7/2B2N2/1PR2PPP/2N2K2 w - - 0 1	2008-02-17	2008-02-17	4	<coloque sua definição aqui>	1
6979	1	3n4/r2nbkpp/2p1pp2/p1PpP3/P7/2BN1N2/1PR2PPP/5K2 w - - 0 1	2008-02-17	2008-02-17	4	<coloque sua definição aqui>	1
6980	1	3n4/r2nbkpp/2p1p3/p1PpPp2/P7/2BN1N2/1PR1KPPP/8 w - - 0 1	2008-02-17	2008-02-17	4	<coloque sua definição aqui>	1
6981	1	2kr1bnr/pbpq4/2n1pp2/3p3p/3P1P1B/2N2N1Q/PPP3PP/2KR1B1R w - - 0 1	2008-02-17	2008-02-17	4	<coloque sua definição aqui>	1
6982	1	3rr1k1/pp3pp1/1qn2np1/8/3p4/PP1R1P2/2P1NQPP/R1B3K1 b - - 0 1	2008-02-17	2008-02-17	4	<coloque sua definição aqui>	1
6983	1	r3k2r/1pq1bpp1/p1bppn1p/8/3QPPP1/2N1B3/PPP1B2P/2KR3R w kq - 0 1	2008-02-17	2008-02-17	6	<coloque sua definição aqui>	1
6984	1	r2q1rk1/4bppp/p2p4/2pP4/3pP3/3Q4/PP1B1PPP/R3R1K1 w - - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	1
6985	1	r2q1rk1/4bppp/p2p4/2pP4/3pP3/3Q4/PP1B1PPP/R3R1K1 w - - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	1
6986	1	rnb2r1k/pp2p2p/2pp2p1/q2P1p2/8/1Pb2NP1/PB2PPBP/R2Q1RK1 w - - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	1
6987	1	r4k2/pb2bp1r/1p1qp2p/3pNp2/3P1P2/2N3P1/PPP1Q2P/2KRR3 w - - 0 1	2008-02-17	2008-02-17	6	<coloque sua definição aqui>	1
6988	1	r2qnrnk/p2b2b1/1p1p2pp/2pPpp2/1PP1P3/PRNBB3/3QNPPP/5RK1 w - - 0 1	2008-02-17	2008-02-17	4	<coloque sua definição aqui>	1
6989	1	r2qk1r1/p4p2/bp2pQp1/1n1pP1Bp/7P/3P2N1/P1R2PP1/2R3K1 w q - 0 1	2008-02-17	2008-02-17	4	<coloque sua definição aqui>	1
6990	1	rn1q1b1r/ppp2B2/7p/4Bk2/3Pn1pP/3Qp3/PPP3P1/RN4K1 w - - 0 1	2008-02-17	2008-02-17	5	<coloque sua definição aqui>	1
6991	1	r2q1b1r/1pN1n1pp/p1n3k1/4Pb2/2BP4/P7/1PP3PP/2BQ1RK1 w - - 0 1	2008-02-17	2008-02-17	6	<coloque sua definição aqui>	1
6992	1	r2nkb1r/1p1b1p1p/pB2p1P1/P2pP3/1P4PQ/3q4/3N3P/5RK1 w kq - 0 1	2008-02-17	2008-02-17	6	<coloque sua definição aqui>	1
6993	1	r1bq1r1k/ppp1N1p1/7p/2bp1pN1/2Bn1B1Q/8/PP3PP1/R3R1K1 w - - 0 1	2008-02-17	2008-02-17	0	<coloque sua definição aqui>	1
6994	1	r2q1rk1/1b2bp2/ppn1p1p1/3P2Np/2P4Q/P2B4/1B3PPP/R4RK1 w - - 0 1	2008-02-17	2008-02-17	6	<coloque sua definição aqui>	1
6995	1	3r3k/1b2rpp1/p2qpN1p/1p6/4pP1Q/P5R1/1PP3PP/5R1K w - - 0 1	2008-02-17	2008-02-17	6	<coloque sua definição aqui>	1
6996	1	r2q1r1k/2p1b1pp/p1n5/1p1Q1bN1/4n3/1BP1B3/PP3PPP/R4RK1 w - - 0 1	2008-02-17	2008-02-17	6	<coloque sua definição aqui>	1
6997	1	7k/3q1prp/4rQ2/p2p4/6P1/4R3/2P2PP1/4R1K1 w - - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	2
6998	1	7k/3q2rp/4pQ2/p2p4/6P1/8/2P2PP1/4R1K1 w - - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	2
6999	1	7k/3q2r1/4RQ2/p2p3p/6P1/8/2P2PP1/6K1 w - - 0 1	2008-02-17	2008-02-17	2	<coloque sua definição aqui>	2
7000	1	8/3q2rk/4RQ2/p2p3P/8/8/2P2PP1/6K1 w - - 0 1	2008-02-17	2008-02-17	2	<coloque sua definição aqui>	2
7002	1	8/2R3pk/2N3rp/1p6/1Pb3pP/4pP2/1r6/R5K1 w - - 0 1	2008-02-17	2008-02-17	2	<coloque sua definição aqui>	2
7003	1	8/2R3pk/2N3rp/1p1b4/1P3PpP/4p3/1r6/R5K1 w - - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	2
7004	1	8/7n/B1N5/8/4pp1k/7P/6K1/8 w - - 0 1	2008-02-17	2008-02-17	2	<coloque sua definição aqui>	2
7005	1	8/8/B4n2/4N3/4pp1k/7P/6K1/8 w - - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	2
7006	1	8/1B6/5n2/4N3/4p2k/5p1P/6K1/8 w - - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	2
7007	1	6r1/5k2/PQ3p1r/3bp3/8/2P2R2/2Pq2PP/7K w - - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	2
7008	1	5kr1/2Q5/P4p1r/3bp3/8/2P2R2/2Pq2PP/7K w - - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	2
7009	1	3Q2r1/6k1/P4p1r/3bp3/8/2P2R2/2Pq2PP/7K w - - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	2
7010	1	6rk/3Q4/P4pr1/3bp3/8/2P3R1/2Pq2PP/7K w - - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	2
7011	1	k1r5/3R1Bp1/pr1p2P1/QP1P1q2/4p2p/5P2/PP2K2P/3R4 w - - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	2
7012	1	k1r5/3R1Bp1/pr1p2P1/QP1P4/4p2p/5q2/PP5P/3RK3 w - - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	2
7013	1	k1r5/3R1Bp1/pQ1p2P1/1P1P4/4p2p/8/PP5P/3RK2q w - - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	2
7014	1	k1r5/3R1Bp1/pQ1p2P1/1P1P4/4p2p/8/PP1K2qP/3R4 w - - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	2
7015	1	5kr1/8/p3QP2/2p5/1pP3qp/2n2KP1/P5R1/8 w - - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	2
7016	1	5k2/8/5P2/2p2K2/ppP5/2n3R1/P7/8 w - - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	2
7017	1	5k2/2R5/5P2/1r2p2p/8/4K3/8/8 w - - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	2
7018	1	5k2/2R5/5P2/1r2p2p/8/4K3/8/8 w - - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	2
7019	1	6R1/p4p2/1p2q2p/8/6Pk/8/PP2r1PK/3Q4 w - - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	2
7020	1	6kr/1q2r1p1/1p2N1Q1/5p2/1P1p4/6R1/7P/2R3K1 w - - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	2
7021	1	6k1/1RQ3p1/3p1p2/1P1Pr2p/7P/1q6/4p1PK/8 b - - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	2
7022	1	8/8/5P1k/8/2K5/pr2r3/4R3/2R5 w - - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	2
7023	1	7R/1p3k2/5p2/7R/6K1/5PP1/6r1/r7 b - - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	2
7024	1	8/P1k3p1/1R4p1/7P/8/6K1/5r1R/r7 w - - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	2
7025	1	5k2/6RR/p7/1pr5/1r6/8/6PP/6K1 w - - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	2
7026	1	8/p2r1k2/8/5p2/6p1/1P2Pr2/P1K1R3/6R1 w - - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	2
7027	1	5k2/5p1p/6p1/5b2/8/8/P1pr1PPP/4RRK1 w - - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	2
7001	1	7k/3q2r1/4R3/p2p1Q1P/3P4/8/2P2PP1/6K1 w KQkq - 0 0	2008-02-17	2008-02-28	4	<coloque sua definição aqui>	2
6931	1	rnbqkb1r/pppp1ppp/4pn2/8/3P4/2P5/PP2PPPP/RNBQKBNR w KQkq - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	0
6932	1	rnbqkbnr/pppp1ppp/8/4p3/8/5NP1/PPPPPP1P/RNBQKB1R b KQkq - 0 1	2008-02-17	2008-02-17	4	<coloque sua definição aqui>	0
6933	1	rnbqkbnr/pp2pppp/2p5/3p2P1/8/8/PPPPPPBP/RNBQK1NR b KQkq - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	0
6934	1	rn1qkbnr/ppp1pppp/8/3p4/2P3b1/8/PP1PPPBP/RNBQK1NR b KQkq - 0 1	2008-02-17	2008-02-17	2	<coloque sua definição aqui>	0
6936	1	rn1qkbnr/ppp2ppp/8/3p4/5p2/6PB/PPPPP2P/RNBQK2R w KQkq - 0 1	2008-02-17	2008-02-17	1	<coloque sua definição aqui>	0
6937	1	r1bqkbnr/pp1ppppp/2n5/8/7Q/2N5/PPP1PPPP/R1B1KBNR b KQkq - 0 1	2008-02-17	2008-02-17	4	<coloque sua definição aqui>	0
6938	1	rnbqkbnr/pppp1ppp/8/4p3/P6P/8/1PPPPPP1/RNBQKBNR b KQkq - 0 1	2008-02-17	2008-02-17	2	<coloque sua definição aqui>	0
6940	1	r1bqkb1r/ppp2ppp/2np1n2/4p3/2P5/1PN1P3/P2P1PPP/R1BQKBNR w KQkq - 0 1	2008-02-17	2008-02-17	2	<coloque sua definição aqui>	0
6942	1	rnbqk1nr/ppp2p1p/3b4/6p1/8/5N2/PPPPP1PP/RNBQKB1R w KQkq - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	0
6943	1	rnbqk2r/ppp2ppp/3b3n/8/3P4/5N2/PPP1P1PP/RNBQKB1R b KQkq - 0 1	2008-02-17	2008-02-17	5	<coloque sua definição aqui>	0
6944	1	rnbqkb1r/ppppp1pp/5n2/8/4pPP1/2N5/PPPP3P/R1BQKBNR b KQkq - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	0
6945	1	rnbqkb1r/pp2pppp/5n2/2pp4/5P2/4PN2/PPPP2PP/RNBQKB1R w KQkq - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	0
6946	1	rnbqkb1r/ppppp1pp/5n2/5p2/4P3/3P1N2/PPP2PPP/RNBQKB1R b KQkq - 0 1	2008-02-17	2008-02-17	4	<coloque sua definição aqui>	0
6947	1	rnbqkb1r/pppppppp/5n2/8/8/5N2/PPPPPPPP/RNBQKB1R w KQkq - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	0
6948	1	rnbqkb1r/p1pppppp/5n2/1p6/8/5NP1/PPPPPP1P/RNBQKB1R w KQkq - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	0
6949	1	rnbqkb1r/pppppp1p/5np1/8/8/5NP1/PPPPPP1P/RNBQKB1R w KQkq - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	0
6950	1	rnbqkbnr/pp2pppp/8/2pp4/8/5NP1/PPPPPP1P/RNBQKB1R w KQkq - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	0
6955	1	rnbqkbnr/pppp1p1p/6p1/4p3/2P1P3/8/PP1P1PPP/RNBQKBNR w KQkq - 0 1	2008-02-17	2008-02-17	2	<coloque sua definição aqui>	0
6957	1	rn1qkb1r/pp2pppp/2p2n2/3p4/2P3b1/1P3NP1/P2PPP1P/RNBQKB1R w KQkq - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	0
6958	1	rn1qkb1r/pp2pppp/2p2n2/3p1b2/2P5/1P3NP1/P2PPP1P/RNBQKB1R w KQkq - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	0
6959	1	rnbqkbnr/pp3ppp/2p1p3/3p4/2P5/5NP1/PP1PPP1P/RNBQKB1R w KQkq - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	0
6962	1	rnbqkb1r/ppp2ppp/4pn2/3pP3/2P5/2N5/PP1P1PPP/R1BQKBNR b KQkq - 0 1	2008-02-17	2008-02-17	6	<coloque sua definição aqui>	0
6960	1	rnbqk2r/ppp1bppp/4pn2/3p4/2P5/5NP1/PP1PPPBP/RNBQ1RK1 b kq - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	0
6961	1	r1bqk2r/pp2bppp/2n1p3/2pn4/8/2N2NP1/PP1PPPBP/R1BQ1RK1 w kq - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	0
6963	1	r1bqkb1r/pppp1ppp/2n1pn2/8/2P1P3/2N5/PP1P1PPP/R1BQKBNR w KQkq - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	0
6964	1	r1bqkb1r/pppp1ppp/2n1pn2/8/2P1P3/2N5/PP1P1PPP/R1BQKBNR w KQkq - 0 1	2008-02-17	2008-02-17	3	<coloque sua definição aqui>	0
7029	1	P7/1P6/2p5/8/8/8/8/8 w KQkq - 0 0	2012-05-25	2012-05-25	0	<coloque um texto de apoio sobre aspectos táticos desta situação>	0
6956	1	rnbqkbnr/pp2pppp/2p5/3p4/2P5/1P3N2/P2PPPPP/RNBQKB1R b KQkq - 0 1	2008-02-17	2012-06-06	3	<coloque sua definição aqui>	0
\.


--
-- TOC entry 2124 (class 0 OID 16472)
-- Dependencies: 175
-- Data for Name: heu_tipo; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY heu_tipo (cod_tipo, cod_classe, txt_nome, txt_descricao) FROM stdin;
1	1	APRENDIZ	Aprendiz de Xadrez
2	1	MONITOR	Aprendiz que auxiliará o Coordenador nas ativades de ensino
4	1	MESTRE	Usuário especialista no contexto do jogo
5	1	JUIZ	Usuário responsável por supervisionar a execução de competições
6	1	ESTATÍSTICO	Usuário responsável por analisar os dados da competições realizadas
3	1	COORDENADOR	Usuário responsável por coordenar as atividades de ensino junto a uma turma de aprendizes
7	2	INICIANTE	Neste nível o aprendiz praticará a alteração dos valores absolutos inicias de cada tipo de Peça.
8	2	BÁSICO	Neste nível o aprendiz poderá definir regiões e criar Heurísticas de Valor de Peça.
9	2	INTERMEDIÁRIO	Neste nível o aprendiz poderá criar Heurísticas de Valor de Jogo.
10	2	PLENO	Neste nível o aprendiz poderá definir novas Etapas e criar os critérios de transições entre elas (Heurísticas de Transição de Etapas).
11	2	AVANÇADO	Neste nível o aprendiz poderá alterar as Expressões de Cálculo Heurísticos de cada Etapa criada.
12	2	ESPECIALISTA	Neste nível o autor poderá alterar e codificar novas funções.
13	3	EXPLICAÇÃO	Texto explicando algum conceito usado na construção do componente
14	3	QUESTÃO	Uma questão direcionada ao autor do componente
15	3	ELOGIO	Um elogio feito sobre o componente criado
16	3	CRÍTICA	Uma crítica feita sobre o componente criado
17	3	NORMAL	Um texto normal
18	4	HEURÍSTICA DE VALOR DE PEÇA	Heurística que Altera o Valor Padrão de um ou mais Tipos de Peças
20	4	HEURÍSTICA DE TRANSIÇÃO DE ETAPA	Heurística que defini uma outra Etapa como Etapa Atual
21	5	ETAPA NORMAL	Etapa normal
22	6	REGIÃO NORMAL	Região normal
23	7	EXPRESSÃO PADRÃO	Expressão de Cálculo Heurístico Padrão para novas Etapas
24	7	EXPRESSÃO DEFINIDA PELO USUÁRIO	Expressão de Cálculo Heurístico definida pelo usuário
25	8	FUNÇÃO BÁSICA TEMPO	Funções que Avaliam a Questão do Tempo de Jogo
26	8	FUNÇÃO BÁSICA POSIÇÃO	Funções que Avaliam as Posições de Peças em determinadas Regiões do Tabuleiro
27	8	FUNÇÃO BÁSICA QUANTIDADE	Funções que Avaliam a Quantidade de Pecas em determinadas Regiões do Tabuleiro
28	8	FUNÇÃO BÁSICA VALOR	Funções que Avaliam o Valor das Peças em determinadas Regiões do Tabuleiro
29	8	FUNÇÃO BÁSICA SITUAÇÃO	Funções que Avaliam a Situação no Jogo de Determinadas Peças
30	1	ADMINISTRADOR	Usuário responsável por administrar o sistema
31	9	PRETAS TEM VANTAGEM DECISIVA	Pretas têm vantagem decisiva
32	9	PRETAS TEM VANTAGEM CLARA	Pretas tem uma vantagem clara
34	9	TUDO IGUAL	Tudo igual
35	9	BRANCAS TEM UMA PEQUENA VANTAGEM	Brancas tem uma vantagem pequena
33	9	PRETAS TEM UMA PEQUENA VANTAGEM	Pretas têm uma vantagem pequena
36	9	BRANCAS TEM UMA VANTAGEM CLARA	Brancas tem uma vantagem clara
37	9	BRANCAS TEM UMA VANTAGEM DECISIVA	Brancas têm vantagem decisiva
38	10	BLOQUEADO	O acesso do Usuário ao sistema está bloqueado
39	10	LIBERADO	O acesso do Usuário ao sistema está liberado
40	10	TROCANDO SENHA	O usuário teve sua senha reiniciada. Precisa definí-la no próximo acesso ao sistema
41	11	BLOQUEADA	A Turma está com acesso Bloqueado
42	11	LIBERADA	A Turma está com o acesso Liberado
19	4	HEURÍSTICA DE VALOR DE TABULEIRO	Heurística que Altera o Valor Heurístico calculado de uma Situação de Jogo
43	12	ERRO NO SISTEMA	Histórico de erro na execução do Sistema
46	12	BUSCOU AJUDA DO SISTEMA	O usuário consultou a ajuda do Sistema
44	12	ENTROU NO SISTEMA	O usuário conectou no Sistema
45	12	SAIU DO SISTEMA	O usuário desconectou do Sistema
47	12	CRIOU COMPONENTE	O usuário criou um novo Componente
48	12	ABRIU COMPONENTE	O usuário abriu um Componente
49	12	ALTEROU COMPONENTE	O usuário alterou um Componente
50	12	EXCLUIU COMPONENTE	O usuário excluiu um Componente
51	12	RELACIONOU COMPONENTES	O usuário relacionou um Componente com outro
52	12	USOU COMPONENTE EM PARTIDA	O usuário usou o Componente em uma Partida Automática
53	12	USOU COMPONENTE EM UMA AVALIAÇÃO	O usuário usou o Componente em uma Avaliação
\.


--
-- TOC entry 2125 (class 0 OID 16479)
-- Dependencies: 176
-- Data for Name: heu_turma; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY heu_turma (cod_turma, txt_nome, txt_descricao, cod_instituicao, bit_permissoes, cod_situacao, dat_criacao, dat_cancelamento) FROM stdin;
\.


--
-- TOC entry 2126 (class 0 OID 16489)
-- Dependencies: 177
-- Data for Name: heu_turma_usuario; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY heu_turma_usuario (cod_turma, cod_usuario, cod_tipo, dat_criacao, dat_cancelamento) FROM stdin;
\.


--
-- TOC entry 2127 (class 0 OID 16493)
-- Dependencies: 178
-- Data for Name: heu_usuario; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY heu_usuario (cod_usuario, cod_tipo, txt_login, txt_senha, txt_nome, txt_foto, log_sexo_masculino, dat_nascimento, txt_email, dat_criacao, log_online, qtd_anotacoes_para_outros, qtd_acessos_realizados, qtd_copias_realizadas, bit_permissoes, dat_cancelamento, cod_situacao) FROM stdin;
1	30	alexandre	7b0e06be40dd33eab35202cd7a0ec7ae	ALEXANDRE RÔMOLO MOREIRA FEITOSA	null	t	1977-04-18	alexandrefeitosa@utfpr.edu.br	2006-07-03 00:00:00	f	0	381	0	0	\N	39
\.


--
-- TOC entry 2066 (class 2606 OID 16522)
-- Name: PK_situacoesdejogo; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY heu_situacoesdejogo
    ADD CONSTRAINT "PK_situacoesdejogo" PRIMARY KEY (cod_situacao);


--
-- TOC entry 2039 (class 2606 OID 16524)
-- Name: pk_avaliacao; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY heu_avaliacoes
    ADD CONSTRAINT pk_avaliacao PRIMARY KEY (cod_avaliacao);


--
-- TOC entry 2043 (class 2606 OID 16526)
-- Name: pk_heu_classe; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY heu_classe
    ADD CONSTRAINT pk_heu_classe PRIMARY KEY (cod_classe);


--
-- TOC entry 2048 (class 2606 OID 16528)
-- Name: pk_heu_componente; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY heu_componente
    ADD CONSTRAINT pk_heu_componente PRIMARY KEY (cod_componente);


--
-- TOC entry 2055 (class 2606 OID 16530)
-- Name: pk_heu_componente_componente; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY heu_componente_componente
    ADD CONSTRAINT pk_heu_componente_componente PRIMARY KEY (cod_componente_principal, cod_componente_incluido);


--
-- TOC entry 2058 (class 2606 OID 16532)
-- Name: pk_heu_desempenho_conj_heu; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY heu_desempenho_conj_heu
    ADD CONSTRAINT pk_heu_desempenho_conj_heu PRIMARY KEY (cod_componente);


--
-- TOC entry 2093 (class 2606 OID 16789)
-- Name: pk_heu_historico; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY heu_historico
    ADD CONSTRAINT pk_heu_historico PRIMARY KEY (cod_historico);


--
-- TOC entry 2061 (class 2606 OID 16534)
-- Name: pk_heu_instituicao; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY heu_instituicao
    ADD CONSTRAINT pk_heu_instituicao PRIMARY KEY (cod_instituicao);


--
-- TOC entry 2070 (class 2606 OID 16536)
-- Name: pk_heu_tipo; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY heu_tipo
    ADD CONSTRAINT pk_heu_tipo PRIMARY KEY (cod_tipo);


--
-- TOC entry 2075 (class 2606 OID 16538)
-- Name: pk_heu_turma; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY heu_turma
    ADD CONSTRAINT pk_heu_turma PRIMARY KEY (cod_turma);


--
-- TOC entry 2086 (class 2606 OID 16540)
-- Name: pk_heu_usuario; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY heu_usuario
    ADD CONSTRAINT pk_heu_usuario PRIMARY KEY (cod_usuario);


--
-- TOC entry 2080 (class 2606 OID 16542)
-- Name: pk_turma_usuario; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY heu_turma_usuario
    ADD CONSTRAINT pk_turma_usuario PRIMARY KEY (cod_turma, cod_usuario);


--
-- TOC entry 2064 (class 2606 OID 16544)
-- Name: unic_nome_instituicao; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY heu_instituicao
    ADD CONSTRAINT unic_nome_instituicao UNIQUE (txt_nome);


--
-- TOC entry 2081 (class 1259 OID 16545)
-- Name: ak_email_usuario; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX ak_email_usuario ON heu_usuario USING btree (txt_email);


--
-- TOC entry 2082 (class 1259 OID 16546)
-- Name: ak_login_usuario; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX ak_login_usuario ON heu_usuario USING btree (txt_login);


--
-- TOC entry 2040 (class 1259 OID 16547)
-- Name: ak_nome_classe; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX ak_nome_classe ON heu_classe USING btree (txt_nome);


--
-- TOC entry 2067 (class 1259 OID 16548)
-- Name: ak_nome_tipo; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX ak_nome_tipo ON heu_tipo USING btree (txt_nome);


--
-- TOC entry 2068 (class 1259 OID 16549)
-- Name: fk_classe; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fk_classe ON heu_tipo USING btree (cod_classe);


--
-- TOC entry 2072 (class 1259 OID 16550)
-- Name: fk_cod_situacao_turma; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fk_cod_situacao_turma ON heu_turma USING btree (cod_situacao);


--
-- TOC entry 2083 (class 1259 OID 16551)
-- Name: fk_cod_situacao_usuario; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fk_cod_situacao_usuario ON heu_usuario USING btree (cod_situacao);


--
-- TOC entry 2090 (class 1259 OID 17169)
-- Name: fk_cod_usuario; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fk_cod_usuario ON heu_historico USING btree (cod_usuario);


--
-- TOC entry 2052 (class 1259 OID 16552)
-- Name: fk_componente_incluido; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fk_componente_incluido ON heu_componente_componente USING btree (cod_componente_incluido);


--
-- TOC entry 2091 (class 1259 OID 17170)
-- Name: fk_cot_tipo; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fk_cot_tipo ON heu_historico USING btree (cod_tipo);


--
-- TOC entry 2059 (class 1259 OID 16553)
-- Name: fk_heu_usuario; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fk_heu_usuario ON heu_instituicao USING btree (cod_usuario);


--
-- TOC entry 2073 (class 1259 OID 16554)
-- Name: fk_instituicao; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fk_instituicao ON heu_turma USING btree (cod_instituicao);


--
-- TOC entry 2044 (class 1259 OID 16555)
-- Name: fk_tipo_componente; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fk_tipo_componente ON heu_componente USING btree (cod_tipo);


--
-- TOC entry 2084 (class 1259 OID 16556)
-- Name: fk_tipo_usuario; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fk_tipo_usuario ON heu_usuario USING btree (cod_tipo);


--
-- TOC entry 2077 (class 1259 OID 16557)
-- Name: fk_usuario; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fk_usuario ON heu_turma_usuario USING btree (cod_usuario);


--
-- TOC entry 2045 (class 1259 OID 16558)
-- Name: fk_usuario_componente; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fk_usuario_componente ON heu_componente USING btree (cod_usuario);


--
-- TOC entry 2078 (class 1259 OID 16559)
-- Name: ph_heu_turma_usuario; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX ph_heu_turma_usuario ON heu_turma_usuario USING btree (cod_turma, cod_usuario);


--
-- TOC entry 2041 (class 1259 OID 16560)
-- Name: pk_classe; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX pk_classe ON heu_classe USING btree (cod_classe);


--
-- TOC entry 2046 (class 1259 OID 16561)
-- Name: pk_componente; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX pk_componente ON heu_componente USING btree (cod_componente);


--
-- TOC entry 2053 (class 1259 OID 16562)
-- Name: pk_componente_componente; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX pk_componente_componente ON heu_componente_componente USING btree (cod_componente_principal, cod_componente_incluido);


--
-- TOC entry 2056 (class 1259 OID 16563)
-- Name: pk_desempenho_conj_heu; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX pk_desempenho_conj_heu ON heu_desempenho_conj_heu USING btree (cod_componente);


--
-- TOC entry 2094 (class 1259 OID 17172)
-- Name: pk_historico; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX pk_historico ON heu_historico USING btree (cod_historico);


--
-- TOC entry 2062 (class 1259 OID 16564)
-- Name: pk_instituicao; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX pk_instituicao ON heu_instituicao USING btree (cod_instituicao);


--
-- TOC entry 2071 (class 1259 OID 16565)
-- Name: pk_tipo; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX pk_tipo ON heu_tipo USING btree (cod_tipo);


--
-- TOC entry 2076 (class 1259 OID 16566)
-- Name: pk_turma; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX pk_turma ON heu_turma USING btree (cod_turma);


--
-- TOC entry 2087 (class 1259 OID 16567)
-- Name: pk_usuario; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX pk_usuario ON heu_usuario USING btree (cod_usuario);


--
-- TOC entry 2095 (class 1259 OID 17168)
-- Name: ui_autor_componente; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX ui_autor_componente ON heu_historico USING btree (cod_autor_componente);


--
-- TOC entry 2096 (class 1259 OID 17166)
-- Name: ui_cod_componente; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX ui_cod_componente ON heu_historico USING btree (cod_tipo_componente);


--
-- TOC entry 2049 (class 1259 OID 16568)
-- Name: ui_data_criacao; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX ui_data_criacao ON heu_componente USING btree (dat_criacao);


--
-- TOC entry 2050 (class 1259 OID 16569)
-- Name: ui_data_modificacao; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX ui_data_modificacao ON heu_componente USING btree (dat_ultima_modificacao);


--
-- TOC entry 2051 (class 1259 OID 16570)
-- Name: ui_nome_componente; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX ui_nome_componente ON heu_componente USING btree (txt_nome);


--
-- TOC entry 2088 (class 1259 OID 16571)
-- Name: ui_nome_usuario; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX ui_nome_usuario ON heu_usuario USING btree (txt_nome);


--
-- TOC entry 2097 (class 1259 OID 17167)
-- Name: ui_tipo_componente; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX ui_tipo_componente ON heu_historico USING btree (cod_tipo_componente);


--
-- TOC entry 2089 (class 1259 OID 16572)
-- Name: ui_usuarios_online; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX ui_usuarios_online ON heu_usuario USING btree (log_online);


--
-- TOC entry 2106 (class 2606 OID 16573)
-- Name: FK_situacoesdejogo; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY heu_situacoesdejogo
    ADD CONSTRAINT "FK_situacoesdejogo" FOREIGN KEY (cod_usuario) REFERENCES heu_usuario(cod_usuario);


--
-- TOC entry 2100 (class 2606 OID 16578)
-- Name: fk_autor_componente; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY heu_componente
    ADD CONSTRAINT fk_autor_componente FOREIGN KEY (cod_usuario) REFERENCES heu_usuario(cod_usuario) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 2098 (class 2606 OID 16684)
-- Name: fk_aval_componente; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY heu_avaliacoes
    ADD CONSTRAINT fk_aval_componente FOREIGN KEY (cod_componente) REFERENCES heu_componente(cod_componente);


--
-- TOC entry 2099 (class 2606 OID 16689)
-- Name: fk_aval_sitjogo; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY heu_avaliacoes
    ADD CONSTRAINT fk_aval_sitjogo FOREIGN KEY (cod_situacao) REFERENCES heu_situacoesdejogo(cod_situacao);


--
-- TOC entry 2107 (class 2606 OID 16679)
-- Name: fk_classe; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY heu_tipo
    ADD CONSTRAINT fk_classe FOREIGN KEY (cod_classe) REFERENCES heu_classe(cod_classe) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 2109 (class 2606 OID 16598)
-- Name: fk_cod_situacao_turma; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY heu_turma
    ADD CONSTRAINT fk_cod_situacao_turma FOREIGN KEY (cod_situacao) REFERENCES heu_tipo(cod_tipo);


--
-- TOC entry 2113 (class 2606 OID 16603)
-- Name: fk_cod_situacao_usuario; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY heu_usuario
    ADD CONSTRAINT fk_cod_situacao_usuario FOREIGN KEY (cod_situacao) REFERENCES heu_tipo(cod_tipo);


--
-- TOC entry 2115 (class 2606 OID 16810)
-- Name: fk_cod_tipo; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY heu_historico
    ADD CONSTRAINT fk_cod_tipo FOREIGN KEY (cod_tipo) REFERENCES heu_tipo(cod_tipo) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 2116 (class 2606 OID 16815)
-- Name: fk_cod_usuario; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY heu_historico
    ADD CONSTRAINT fk_cod_usuario FOREIGN KEY (cod_usuario) REFERENCES heu_usuario(cod_usuario) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 2102 (class 2606 OID 16608)
-- Name: fk_componente_incluido; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY heu_componente_componente
    ADD CONSTRAINT fk_componente_incluido FOREIGN KEY (cod_componente_incluido) REFERENCES heu_componente(cod_componente) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 2103 (class 2606 OID 16613)
-- Name: fk_componente_principal; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY heu_componente_componente
    ADD CONSTRAINT fk_componente_principal FOREIGN KEY (cod_componente_principal) REFERENCES heu_componente(cod_componente) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 2104 (class 2606 OID 16618)
-- Name: fk_conjunto_heuristico; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY heu_desempenho_conj_heu
    ADD CONSTRAINT fk_conjunto_heuristico FOREIGN KEY (cod_componente) REFERENCES heu_componente(cod_componente) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 2105 (class 2606 OID 16623)
-- Name: fk_coordenador_instituicao; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY heu_instituicao
    ADD CONSTRAINT fk_coordenador_instituicao FOREIGN KEY (cod_usuario) REFERENCES heu_usuario(cod_usuario);


--
-- TOC entry 2108 (class 2606 OID 16628)
-- Name: fk_instituicao; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY heu_turma
    ADD CONSTRAINT fk_instituicao FOREIGN KEY (cod_instituicao) REFERENCES heu_instituicao(cod_instituicao);


--
-- TOC entry 2110 (class 2606 OID 16633)
-- Name: fk_tipo; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY heu_turma_usuario
    ADD CONSTRAINT fk_tipo FOREIGN KEY (cod_tipo) REFERENCES heu_tipo(cod_tipo);


--
-- TOC entry 2101 (class 2606 OID 16638)
-- Name: fk_tipo_componente; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY heu_componente
    ADD CONSTRAINT fk_tipo_componente FOREIGN KEY (cod_tipo) REFERENCES heu_tipo(cod_tipo) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 2114 (class 2606 OID 16643)
-- Name: fk_tipo_usuario; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY heu_usuario
    ADD CONSTRAINT fk_tipo_usuario FOREIGN KEY (cod_tipo) REFERENCES heu_tipo(cod_tipo) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 2111 (class 2606 OID 16648)
-- Name: fk_turma; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY heu_turma_usuario
    ADD CONSTRAINT fk_turma FOREIGN KEY (cod_turma) REFERENCES heu_turma(cod_turma);


--
-- TOC entry 2112 (class 2606 OID 16653)
-- Name: fk_usuario; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY heu_turma_usuario
    ADD CONSTRAINT fk_usuario FOREIGN KEY (cod_usuario) REFERENCES heu_usuario(cod_usuario);


--
-- TOC entry 2135 (class 0 OID 0)
-- Dependencies: 6
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- TOC entry 2137 (class 0 OID 0)
-- Dependencies: 168
-- Name: heu_avaliacoes; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE heu_avaliacoes FROM PUBLIC;
REVOKE ALL ON TABLE heu_avaliacoes FROM postgres;
GRANT ALL ON TABLE heu_avaliacoes TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE heu_avaliacoes TO sistema_heuchess;


--
-- TOC entry 2138 (class 0 OID 0)
-- Dependencies: 169
-- Name: heu_classe; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE heu_classe FROM PUBLIC;
REVOKE ALL ON TABLE heu_classe FROM postgres;
GRANT ALL ON TABLE heu_classe TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE heu_classe TO sistema_heuchess;


--
-- TOC entry 2139 (class 0 OID 0)
-- Dependencies: 170
-- Name: heu_componente; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE heu_componente FROM PUBLIC;
REVOKE ALL ON TABLE heu_componente FROM postgres;
GRANT ALL ON TABLE heu_componente TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE heu_componente TO sistema_heuchess;


--
-- TOC entry 2140 (class 0 OID 0)
-- Dependencies: 171
-- Name: heu_componente_componente; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE heu_componente_componente FROM PUBLIC;
REVOKE ALL ON TABLE heu_componente_componente FROM postgres;
GRANT ALL ON TABLE heu_componente_componente TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE heu_componente_componente TO sistema_heuchess;


--
-- TOC entry 2141 (class 0 OID 0)
-- Dependencies: 172
-- Name: heu_desempenho_conj_heu; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE heu_desempenho_conj_heu FROM PUBLIC;
REVOKE ALL ON TABLE heu_desempenho_conj_heu FROM postgres;
GRANT ALL ON TABLE heu_desempenho_conj_heu TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE heu_desempenho_conj_heu TO sistema_heuchess;


--
-- TOC entry 2142 (class 0 OID 0)
-- Dependencies: 183
-- Name: heu_historico; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE heu_historico FROM PUBLIC;
REVOKE ALL ON TABLE heu_historico FROM postgres;
GRANT ALL ON TABLE heu_historico TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE heu_historico TO sistema_heuchess;


--
-- TOC entry 2143 (class 0 OID 0)
-- Dependencies: 173
-- Name: heu_instituicao; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE heu_instituicao FROM PUBLIC;
REVOKE ALL ON TABLE heu_instituicao FROM postgres;
GRANT ALL ON TABLE heu_instituicao TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE heu_instituicao TO sistema_heuchess;


--
-- TOC entry 2147 (class 0 OID 0)
-- Dependencies: 174
-- Name: heu_situacoesdejogo; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE heu_situacoesdejogo FROM PUBLIC;
REVOKE ALL ON TABLE heu_situacoesdejogo FROM postgres;
GRANT ALL ON TABLE heu_situacoesdejogo TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE heu_situacoesdejogo TO sistema_heuchess;


--
-- TOC entry 2148 (class 0 OID 0)
-- Dependencies: 175
-- Name: heu_tipo; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE heu_tipo FROM PUBLIC;
REVOKE ALL ON TABLE heu_tipo FROM postgres;
GRANT ALL ON TABLE heu_tipo TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE heu_tipo TO sistema_heuchess;


--
-- TOC entry 2149 (class 0 OID 0)
-- Dependencies: 176
-- Name: heu_turma; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE heu_turma FROM PUBLIC;
REVOKE ALL ON TABLE heu_turma FROM postgres;
GRANT ALL ON TABLE heu_turma TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE heu_turma TO sistema_heuchess;


--
-- TOC entry 2150 (class 0 OID 0)
-- Dependencies: 177
-- Name: heu_turma_usuario; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE heu_turma_usuario FROM PUBLIC;
REVOKE ALL ON TABLE heu_turma_usuario FROM postgres;
GRANT ALL ON TABLE heu_turma_usuario TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE heu_turma_usuario TO sistema_heuchess;


--
-- TOC entry 2151 (class 0 OID 0)
-- Dependencies: 178
-- Name: heu_usuario; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE heu_usuario FROM PUBLIC;
REVOKE ALL ON TABLE heu_usuario FROM postgres;
GRANT ALL ON TABLE heu_usuario TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE heu_usuario TO sistema_heuchess;


--
-- TOC entry 2153 (class 0 OID 0)
-- Dependencies: 179
-- Name: seq_heu_avaliacao; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON SEQUENCE seq_heu_avaliacao FROM PUBLIC;
REVOKE ALL ON SEQUENCE seq_heu_avaliacao FROM postgres;
GRANT ALL ON SEQUENCE seq_heu_avaliacao TO postgres;
GRANT SELECT,UPDATE ON SEQUENCE seq_heu_avaliacao TO sistema_heuchess;


--
-- TOC entry 2155 (class 0 OID 0)
-- Dependencies: 180
-- Name: seq_heu_componente; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON SEQUENCE seq_heu_componente FROM PUBLIC;
REVOKE ALL ON SEQUENCE seq_heu_componente FROM postgres;
GRANT ALL ON SEQUENCE seq_heu_componente TO postgres;
GRANT SELECT,UPDATE ON SEQUENCE seq_heu_componente TO sistema_heuchess;


--
-- TOC entry 2158 (class 0 OID 0)
-- Dependencies: 184
-- Name: seq_heu_historico; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON SEQUENCE seq_heu_historico FROM PUBLIC;
REVOKE ALL ON SEQUENCE seq_heu_historico FROM postgres;
GRANT ALL ON SEQUENCE seq_heu_historico TO postgres;
GRANT SELECT,UPDATE ON SEQUENCE seq_heu_historico TO sistema_heuchess;


--
-- TOC entry 2160 (class 0 OID 0)
-- Dependencies: 181
-- Name: seq_heu_situacaodejogo; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON SEQUENCE seq_heu_situacaodejogo FROM PUBLIC;
REVOKE ALL ON SEQUENCE seq_heu_situacaodejogo FROM postgres;
GRANT ALL ON SEQUENCE seq_heu_situacaodejogo TO postgres;
GRANT SELECT,UPDATE ON SEQUENCE seq_heu_situacaodejogo TO sistema_heuchess;


--
-- TOC entry 2162 (class 0 OID 0)
-- Dependencies: 182
-- Name: seq_heu_usuario; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON SEQUENCE seq_heu_usuario FROM PUBLIC;
REVOKE ALL ON SEQUENCE seq_heu_usuario FROM postgres;
GRANT ALL ON SEQUENCE seq_heu_usuario TO postgres;
GRANT SELECT,UPDATE ON SEQUENCE seq_heu_usuario TO sistema_heuchess;


-- Completed on 2012-11-22 14:32:05

--
-- PostgreSQL database dump complete
--

