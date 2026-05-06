# Analise-e-Teste-de-Software
a106927 - Tiago Martins  
a106894 - Francisco Barros  

## Projeto 1 (SpotifyUM / Maven)

> **Resumo rápido:**  
> - **JUnit5 + jqwik** → Java 17  
> - **EvoSuite** (gerar + correr) → Java 8  
> - **JaCoCo** → Java 17  
> - **PIT** → Java 17  

---

## ✅ 1) Suite normal (JUnit 5)
Corre os testes unitários “manuais” + property-based (jqwik):

```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH="$JAVA_HOME/bin:$PATH"

mvn -f "Projeto1/SpotifyUM/pom.xml" clean test
```

Inclui o teste property-based em:
`Projeto1/SpotifyUM/src/test/java/org/PropertyBased/SpotifUMPropertyBasedTest.java`

---

## ✅ 2) Cobertura (JaCoCo)
Gera o relatório de cobertura:

```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH="$JAVA_HOME/bin:$PATH"

mvn -f "Projeto1/SpotifyUM/pom.xml" clean verify
```

Relatório HTML:
`Projeto1/SpotifyUM/target/site/jacoco/index.html`

---

## ✅ 3) Qualidade por mutação (PIT)
Gera relatório de mutação:

```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH="$JAVA_HOME/bin:$PATH"

mvn -f "Projeto1/SpotifyUM/pom.xml" org.pitest:pitest-maven:mutationCoverage
```

Relatório HTML:
`Projeto1/SpotifyUM/target/pit-reports/index.html`

---

## ✅ 4) EvoSuite — gerar testes (Java 8)
**Obrigatório usar Java 8**.

```bash
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export PATH="$JAVA_HOME/bin:$PATH"

rm -rf "Projeto1/SpotifyUM/.evosuite"

mvn -f "Projeto1/SpotifyUM/pom.xml" -P evosuite-generate -Dmaven.test.skip=true clean test
```

Os testes são exportados para:
`Projeto1/SpotifyUM/target/generated-test-sources/evosuite/`

---

## ✅ 5) EvoSuite — copiar testes gerados
```bash
cp -r "Projeto1/SpotifyUM/target/generated-test-sources/evosuite/org" \
      "Projeto1/SpotifyUM/src/test/java/"
```

---

## ✅ 6) EvoSuite — correr testes gerados (Java 8)
```bash
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export PATH="$JAVA_HOME/bin:$PATH"

mvn -f "Projeto1/SpotifyUM/pom.xml" -P evosuite-run clean test
```

---

## ✅ 7) Voltar aos testes normais (Java 17)
```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH="$JAVA_HOME/bin:$PATH"

mvn -f "Projeto1/SpotifyUM/pom.xml" clean test
```

---

## Notas importantes sobre o EvoSuite
- O EvoSuite 1.0.3 **não funciona com bytecode Java 17**.
- Por isso **gerar e correr testes EvoSuite tem de ser em Java 8**.
- Os ficheiros `*_ESTest*.java` são excluídos da suite normal por defeito.

---

## Projeto 2 (Gradle)

> **Resumo rápido:**  
> - **JUnit5 + jqwik** → Gradle  
> - **JaCoCo** → Gradle  
> - **PIT** → Gradle  
> - **EvoSuite** → task `evosuiteGenerate` (Docker)  
> - **Pipeline completo** → task `atsFullPipeline`

---

## ✅ 1) Suite normal (JUnit 5)
```bash
cd Projeto2
./gradlew clean test
```

---

## ✅ 2) Cobertura (JaCoCo)
```bash
cd Projeto2
./gradlew jacocoTestReport
```

Relatório HTML:
`Projeto2/build/reports/jacoco/test/html/index.html`

---

## ✅ 3) Qualidade por mutação (PIT)
```bash
cd Projeto2
./gradlew pitest
```

Relatório HTML:
`Projeto2/build/reports/pitest/index.html`

---

## ✅ 4) EvoSuite — gerar testes
```bash
cd Projeto2
./gradlew evosuiteGenerate
```

---

## ✅ 5) Pipeline completo (JUnit + JaCoCo + PIT + EvoSuite)
```bash
cd Projeto2
./gradlew atsFullPipeline
```