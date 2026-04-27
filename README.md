# Analise-e-Teste-de-Software
a106927 - Tiago Martins
a106894 - Francisco Barros

## Projeto 1 (SpotifyUM / Maven)

### 1) Suite normal (JUnit 5)
Corre os testes unitários “manuais” (baseline):
```bash
mvn -f "Projeto1/SpotifyUM/pom.xml" test
```

Inclui também testes *property-based* (estilo QuickCheck) com jqwik em `Projeto1/SpotifyUM/src/test/java/org/PropertyBased/SpotifUMPropertyBasedTest.java`.

### 2) Cobertura (JaCoCo)
Gera o relatório de cobertura em `Projeto1/SpotifyUM/target/site/jacoco/`:
```bash
mvn -f "Projeto1/SpotifyUM/pom.xml" verify
```
Relatório HTML: `Projeto1/SpotifyUM/target/site/jacoco/index.html`.

### 3) Qualidade por mutação (PIT)
Gera o relatório de mutação em `Projeto1/SpotifyUM/target/pit-reports/`:
```bash
mvn -f "Projeto1/SpotifyUM/pom.xml" org.pitest:pitest-maven:mutationCoverage
```
Relatório HTML: `Projeto1/SpotifyUM/target/pit-reports/index.html`.

### 4) Geração automática (EvoSuite)
O profile `evosuite-generate` está configurado (no `pom.xml`) para gerar testes apenas para as CUTs definidas em `evosuite.cuts`.

Notas importantes:
- O `evosuite-maven-plugin:1.0.3` requer Java 8 (precisa de `tools.jar`).
- Ao ativar profiles manualmente, o profile `tests-junit5` (que é `activeByDefault`) deixa de ser ativado automaticamente; por isso, ao correr EvoSuite deve-se incluir também `tests-junit5`.
- Os ficheiros `*_ESTest*.java` existentes no repositório são mantidos como evidência/artefactos, mas estão excluídos por defeito da compilação/execução da suite normal (para manter o baseline estável).

Comando para gerar/exportar com Java 8 (sem correr testes):
```bash
rm -rf "Projeto1/SpotifyUM/.evosuite" \
	&& find "Projeto1/SpotifyUM/src/test/java" -name '*_ESTest*.java' -delete \
	&& JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64 \
		 PATH="/usr/lib/jvm/java-8-openjdk-amd64/bin:$PATH" \
		 mvn -f "Projeto1/SpotifyUM/pom.xml" -Ptests-junit5,evosuite-generate -DskipTests test
```

Se quiseres apenas correr o baseline depois da geração:
```bash
mvn -f "Projeto1/SpotifyUM/pom.xml" test
```
