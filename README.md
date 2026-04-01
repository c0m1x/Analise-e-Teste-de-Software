# Analise-e-Teste-de-Software
a106927 - Tiago Martins
a106894 - Francisco Barros

## Projeto1 - correr todos os testes

### Suite normal (JUnit)
```bash
mvn -f "Projeto1/SpotifyUM/pom.xml" clean test
```

### EvoSuite clássico para todo o `org.Model` (limpar -> gerar -> exportar -> correr)
> Nota: a geração EvoSuite no ambiente atual deve ser feita com Java 8.
> O `pom.xml` já está configurado para usar 4 cores no EvoSuite.

#### 1) Gerar e exportar os testes EvoSuite a partir de zero
```bash
rm -rf "Projeto1/SpotifyUM/.evosuite" && find "Projeto1/SpotifyUM/src/test/java/org/Model" -name '*_ESTest*.java' -delete && JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64 PATH="/usr/lib/jvm/java-8-openjdk-amd64/bin:$PATH" mvn -f "Projeto1/SpotifyUM/pom.xml" -P evosuite-generate process-test-sources
```

O profile `evosuite-generate` está configurado para estas CUTs do Projeto 1:
- `org.Model.SpotifUM`
- `org.Model.Album.Album`
- `org.Model.Music.Music`
- `org.Model.Music.MusicMultimedia`
- `org.Model.Music.MusicReproduction`
- `org.Model.Plan.PlanFree`
- `org.Model.Plan.PlanPremiumBase`
- `org.Model.Plan.PlanPremiumTop`
- `org.Model.Playlist.Playlist`
- `org.Model.Playlist.PlaylistCreator`
- `org.Model.Playlist.PlaylistFavorites`
- `org.Model.Playlist.PlaylistRandom`
- `org.Model.User.User`

> Nota: `org.Model.Plan.Plan` é abstrata, por isso não faz sentido gerar testes diretos para essa classe.

#### 2) Correr todos os testes do Projeto 1, incluindo os testes EvoSuite exportados
```bash
mvn -f "Projeto1/SpotifyUM/pom.xml" -P tests-junit5,evosuite-runtime test
```

### Comando completo recomendado
```bash
rm -rf "Projeto1/SpotifyUM/.evosuite" && find "Projeto1/SpotifyUM/src/test/java/org/Model" -name '*_ESTest*.java' -delete && JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64 PATH="/usr/lib/jvm/java-8-openjdk-amd64/bin:$PATH" mvn -f "Projeto1/SpotifyUM/pom.xml" -P evosuite-generate process-test-sources && mvn -f "Projeto1/SpotifyUM/pom.xml" -P tests-junit5,evosuite-runtime test
```

### Resultado esperado
No estado atual do projeto, a geração EvoSuite produz testes para todo o modelo concreto de `Projeto1`, e a execução final corre:
- os testes JUnit existentes;
- os testes EvoSuite exportados;
- com o total de testes a variar conforme as CUTs geradas.
