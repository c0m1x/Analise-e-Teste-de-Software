# Guia de Implementacao - Projeto 2 (ATS)

## Data
- 2026-03-31

## Objetivo
- Implementar integralmente o workflow ATS para o Projeto 2, cobrindo os requisitos obrigatorios e extras: JUnit, EvoSuite, cobertura, mutacao, property-based testing, automacao e base de relatorio.

## Mapeamento do enunciado ATS
1. Escrever/complementar testes JUnit.
- Estado: EM EXECUCAO (forte progresso)
- Evidencia:
	- Suite expandida para 141 testes totais.
	- Novos testes para utilitarios de serializacao e adapters em:
		- `src/test/java/org/spotifumtp37/util/LocalDateTimeAdapterTest.java`
		- `src/test/java/org/spotifumtp37/util/SubscriptionPlanAdapterTest.java`
		- `src/test/java/org/spotifumtp37/util/SongTypeAdapterTest.java`
		- `src/test/java/org/spotifumtp37/util/JsonDataParserTest.java`
		- `src/test/java/org/spotifumtp37/util/StatsPropertyTest.java`
	- Reforco dirigido de casos em `src/test/java/org/spotifumtp37/util/StatsTest.java` para cenarios temporais e criadores nulos.

2. Utilizar EvoSuite para gerar testes automaticamente.
- Estado: IMPLEMENTADO COM LIMITACAO DE FERRAMENTA
- Evidencia:
	- Integracao de tarefa `evosuiteGenerate` no Gradle (`build.gradle.kts`).
	- Integracao na pipeline unica `atsFullPipeline`.
- Limitacao observada:
	- A imagem oficial `evosuite/evosuite:latest` (EvoSuite 1.2.0) executa com runtime Java compativel com class file <= 52 e nao consegue instrumentar classes compiladas com Java 17 (class file 61).
	- Resultado: tarefa executa, mas a geracao para classes alvo falha por incompatibilidade de versao de bytecode.

3. Analisar cobertura e qualidade dos testes.
- Estado: IMPLEMENTADO
- Evidencia:
	- JaCoCo configurado e executado automaticamente apos testes.
	- Metricas atuais:
		- LINE: 682 cobertas / 1022 falhadas (40.02%)
		- BRANCH: 263 cobertas / 339 falhadas (43.69%)

4. Utilizar PIT para mutacao.
- Estado: IMPLEMENTADO
- Evidencia:
	- PIT integrado e executado pela pipeline.
	- Metricas atuais:
		- Mutacoes geradas: 890
		- Mutacoes mortas: 265 (30%)
		- Mutacoes sem cobertura: 587
		- Test strength: 87%

5. Property-based testing (QuickCheck/Hypothesis equivalente em Java).
- Estado: IMPLEMENTADO (jqwik)
- Evidencia:
	- Dependencia jqwik integrada no build.
	- Testes de propriedade em:
		- `src/test/java/org/spotifumtp37/model/playlist/PlaylistPropertyTest.java`

6. Relatorio com graficos e analise.
- Estado: BASE DOCUMENTAL IMPLEMENTADA
- Evidencia:
	- Template Overleaf criado em `docs/RELATORIO_ATS_TEMPLATE.tex`.
	- Artefactos de relatorio gerados automaticamente em `build/reports/` (tests, jacoco, pitest).

## Extras do enunciado
1. Automacao de processo com um comando.
- Estado: IMPLEMENTADO
- Evidencia:
	- `Makefile` com alvos `test`, `coverage`, `mutation`, `property`, `evosuite`, `full`.
	- Script unico `scripts/ats_full_pipeline.sh`.
	- Tarefa Gradle unica `atsFullPipeline`.

2. Refactoring + regressao.
- Estado: IMPLEMENTADO
- Evidencia:
	- Refatoracao sem alteracao de comportamento em `src/main/java/org/spotifumtp37/util/Stats.java`:
		- extracao de validacao repetida para helper `isNullOrEmpty`;
		- normalizacao de naming local (`genreCounter`).
	- Validacao de regressao por execucao completa de `test + jacoco + pitest` apos refatoracao.

3. Property-based testing em Java.
- Estado: IMPLEMENTADO (fase inicial)
- Pendente:
	- Alargar propriedades para modulos adicionais (ex: Stats e regras de pontos/plano).

4. Bom relatorio.
- Estado: EM PREPARACAO
- Evidencia:
	- Estrutura de relatorio criada e pipeline de artefactos pronta.

## Alteracoes realizadas nesta iteracao
1. `build.gradle.kts`
- Integracao de task EvoSuite via Docker.
- Integracao de tarefa agregada `atsFullPipeline`.

2. `src/test/java/org/spotifumtp37/model/playlist/PlaylistTest.java`
- Correcao do teste `setSongs()` para respeitar semantica real de substituicao da lista.

3. `src/test/java/org/spotifumtp37/model/playlist/PlaylistPropertyTest.java`
- Testes property-based jqwik para substituicao e copia defensiva.

4. Novos testes utilitarios
- `src/test/java/org/spotifumtp37/util/LocalDateTimeAdapterTest.java`
- `src/test/java/org/spotifumtp37/util/SubscriptionPlanAdapterTest.java`
- `src/test/java/org/spotifumtp37/util/SongTypeAdapterTest.java`
- `src/test/java/org/spotifumtp37/util/JsonDataParserTest.java`

5. Automacao e documentacao
- `Makefile`
- `scripts/ats_full_pipeline.sh`
- `docs/RELATORIO_ATS_TEMPLATE.tex` (template Overleaf completo)

6. Relatorio Overleaf
- O template anterior em markdown foi substituido por um template em LaTeX pronto para Overleaf.
- O template markdown foi removido para evitar duplicacao e manter apenas a versao final em `.tex`.

## Comandos de execucao
- `./gradlew test --no-daemon`
- `./gradlew test jacocoTestReport --no-daemon`
- `./gradlew pitest --no-daemon`
- `./gradlew evosuiteGenerate --no-daemon`
- `./gradlew atsFullPipeline --no-daemon`
- `make full`

## Estado atual resumido
- Testes totais: 141
- Suite JUnit: OK
- JaCoCo: OK
- PIT: OK
- EvoSuite: integrado e executado, mas bloqueado por incompatibilidade de bytecode (ferramenta/runtime)

## Pendencias reais apos verificacao com enunciado
1. Fechar secao de figuras/prints no relatorio final com capturas exportadas de `build/reports`.
2. Melhorar iterativamente mutation score com foco nos pacotes de negocio (fora de UI/delegate).

## Proximos passos imediatos
1. Aplicar um refactoring de codigo de producao com beneficio de legibilidade/manutencao e validar regressao (extra 2).
2. Alargar property-based testing para regras de negocio de Stats/pontos.
3. Fechar relatorio final com graficos extraidos de `build/reports`.