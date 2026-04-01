package org.View;

import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.sound.sampled.Clip;
import org.Controller.*;
import org.Controller.dtos.MusicInfo;
import org.Utils.MusicPlayer;
import org.Utils.BrowserOpener;

/**
 * Classe que representa o menu de reprodução de músicas.
 * Permite selecionar e ouvir músicas e playlists.
 */
public class ReproductionMenu extends Menu {

    /**
     * Enumeração das ações possíveis do player.
     */
    private enum PlayerAction {
        NEXT, PREVIOUS, QUIT, NONE
    }

    /**
     * Construtor do menu de reprodução.
     * @param controller Controlador principal da aplicação.
     * @param menuManager Gestor de menus.
     */
    public ReproductionMenu(Controller controller, MenuManager menuManager) {
        super(controller, menuManager);
    }

    /**
     * Mostra o menu de reprodução e trata a seleção do utilizador.
     */
    @Override
    public void show() {
        System.out.println("\n🎵━━━━━━━━━━━━━━━【 MENU DE REPRODUÇÃO 】━━━━━━━━━━━━━━━🎵\n");
        System.out.printf("%-5s %s\n", "1️⃣", "▶️   Reproduzir Playlist Aleatória");
        if (this.getController().canCurrentUserChooseWhatToPlay()) {
            System.out.printf("%-5s %s\n", "2️⃣", "🎶  Reproduzir Música");
            System.out.printf("%-5s %s\n", "3️⃣", "💿  Reproduzir Álbum");
            System.out.printf("%-5s %s\n", "4️⃣", "📚  Reproduzir Playlist da Sua Biblioteca");
        }
        if(this.getController().currentUserAccessToFavorites())
            System.out.printf("%-5s %s\n", "5️⃣", "✨  Feito para você");
        System.out.printf("%-5s %s\n", "0️⃣", "🔙  Voltar");
        System.out.print("\n👉 Escolha uma opção: ");
        handleInput();
    }

    /**
     * Lê e processa a opção escolhida pelo utilizador.
     */
    @Override
    public void handleInput() {
        try {
            int choice = getScanner().nextInt();
            getScanner().nextLine();
            cleanTerminal();
            switch (choice) {
                case 1:
                    playRandomPlaylist();
                    break;
                case 2:
                    if (this.getController().canCurrentUserChooseWhatToPlay()) playMusic();
                    else System.out.println("\n🚫 Opção inválida. Tente novamente.");
                    break;
                case 3:
                    if (this.getController().canCurrentUserChooseWhatToPlay()) playAlbum();
                    else System.out.println("\n🚫 Opção inválida. Tente novamente.");
                    break;
                case 4:
                    if (this.getController().canCurrentUserChooseWhatToPlay()) playPlaylist();
                    else System.out.println("\n🚫 Opção inválida. Tente novamente.");
                    break;
                case 5:
                    if(this.getController().currentUserAccessToFavorites()) playFavoritePlaylist();
                    else System.out.println("\n🚫 Opção inválida. Tente novamente.");
                    break;
                case 0:
                    System.out.println("\n🔙 A voltar ao menu principal...");
                    getMenuManager().setMenu(new MainMenu(getController(), getMenuManager()));
                    break;
                default:
                    System.out.println("\n🚫 Opção inválida. Tente novamente.");
            }
        } catch (Exception e) {
            System.out.println("\n🚫 Por favor, insira apenas números.");
            getScanner().nextLine();
        }
    }

    // ===================== MÉTODOS DE REPRODUÇÃO =====================

    /**
     * Permite ao utilizador escolher e reproduzir uma música específica.
     */
    private void playMusic() {
        System.out.println(this.getController().listAllMusics());
        System.out.print("\n🎶 Introduza o nome da música: ");
        String musicName = getScanner().nextLine();
        if (!this.getController().musicExists(musicName)) {
            System.out.println("\n🚫 Música não encontrada.");
            return;
        }
        List<String> musicList = Arrays.asList(musicName);
        playMusicList(musicList, musicName);
    }

    /**
     * Permite ao utilizador escolher e reproduzir um álbum.
     * Pergunta se deseja modo aleatório.
     */
    private void playAlbum() {
        System.out.println(this.getController().listAllAlbums());
        System.out.print("\n💿 Nome do álbum a reproduzir: ");
        String albumName = getScanner().nextLine();
        Optional<List<String>> musicNames = getController().getAlbumMusicNames(albumName);
        if (!musicNames.isPresent()) {
            System.out.println("\n🚫 Álbum não encontrado.");
            return;
        }
        List<String> musics = musicNames.get();
        if (askConfirmation("Deseja reproduzir o álbum em modo Aleatório?")) {
            Collections.shuffle(musics);
            System.out.println("\n🔀 Álbum em modo Aleatório.");
        } else {
            System.out.println("\n▶️ Álbum em modo Normal.");
        }
        playMusicList(musics, albumName);
    }

    /**
     * Permite ao utilizador escolher e reproduzir uma playlist da sua biblioteca.
     * Pergunta se deseja modo aleatório.
     */
    private void playPlaylist() {
        System.out.println(this.getController().listUserPlaylists());
        System.out.print("\n📚 Id da playlist a reproduzir: ");
        int playlistId = getScanner().nextInt();
        getScanner().nextLine(); // Limpar buffer


        Optional<List<String>> musicNames = this.getController().getPlaylistMusicNames(playlistId);
        if (!musicNames.isPresent()) {
            System.out.println("\n🚫 Playlist não encontrada.");
            return;
        }
        List<String> musics = musicNames.get();
        if (askConfirmation("Deseja reproduzir a playlist em modo Aleatório?")) {
            Collections.shuffle(musics);
            System.out.println("\n🔀 Playlist em modo Aleatório.");
        } else {
            System.out.println("\n▶️ Playlist em modo Normal.");
        }
        playMusicList(musics, this.getController().getPlaylistId(playlistId));
    }

    /**
     * Reproduz uma playlist aleatória.
     */
    public void playRandomPlaylist() {
        Optional<List<String>> musicNames = this.getController().getRandomPlaylistMusicNames();
        if (!musicNames.isPresent()) {
            System.out.println("\n🚫 Não existem playlists aleatórias disponíveis.");
            return;
        }
        List<String> musics = musicNames.get();
        playMusicList(musics, "Playlist Aleatória");
    }

    /**
     * Gera e reproduz uma playlist personalizada "Feito para você".
     * Pergunta se só quer músicas explícitas e se quer limitar a duração.
     */
    public void playFavoritePlaylist() {
        boolean explicit = askConfirmation("🔞 Quer apenas músicas explícitas?");
        int duration;
        if (askConfirmation("⏳ Quer adicionar um limite de duração à playlist?")) {
            System.out.print("⏱️  Duração máxima da playlist (em minutos): ");
            int maxDuration = getScanner().nextInt();
            getScanner().nextLine(); // Limpar buffer
            duration = maxDuration * 60;
        } else {
            System.out.println("⏱️  Por defeito, a duração máxima é de 60 minutos.");
            duration = 60 * 60;
        }
        Optional<List<String>> musicNames = this.getController().getFavoritePlaylistMusicNames(explicit, duration);
        if (!musicNames.isPresent()) {
            System.out.println("\n🚫 Não existem músicas disponíveis para a playlist.");
            return;
        }
        List<String> musics = musicNames.get();
        playMusicList(musics, "✨ Feito para você");
    }

    /**
     * Reproduz uma lista de músicas, em modo áudio ou letra, com navegação.
     * @param musics Lista de nomes das músicas
     * @param name Nome da playlist/álbum
     */
    private void playMusicList(List<String> musics, String name) {
        try {
            boolean isLyricsMode = askConfirmation("Deseja reproduzir em modo de Letra?");
            System.out.println("\n🎧 A tocar: \"" + name + "\"");
            System.out.println("💡 Comandos disponíveis:");
            System.out.println(getController().canCurrentUserSkip()
                ? "▶️ 'f' → próxima | ⏪ 'b' → anterior | ❌ 'q' → sair\n"
                : "❌ 'q' → sair\n");
            player(musics, isLyricsMode);
        } catch (Exception e) {
            System.out.println("\n❌ Erro ao reproduzir: " + e.getMessage());
        }
    }

    /**
     * Controla a navegação entre músicas, chamando o modo de reprodução adequado.
     * @param musicNames Lista de nomes das músicas
     * @param lyricsMode true para modo letra, false para modo áudio
     */
    private void player(List<String> musicNames, boolean lyricsMode) {
        int index = 0;
        boolean running = true;
        boolean lastSong = false;
        while (running && index < musicNames.size() && index >= 0) {
            if (index == musicNames.size() - 1) {
                lastSong = true;
            }
            System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.printf("\uD83C\uDFB5  Música %d de %d\n", index + 1, musicNames.size());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            MusicInfo mf = this.getController().playMusic(musicNames.get(index));
            PlayerAction result;
            if (!lyricsMode) {
                result = playAudioSingle(mf, musicNames.get(index));
                if (result == PlayerAction.NONE) {
                    System.out.println("\n\uD83D\uDD0A  Áudio indisponível. A mostrar letra!\n");
                    result = playLyricsSingle(mf);
                }
            } else {
                result = playLyricsSingle(mf);
            }
            if (result == PlayerAction.NEXT) {
                if (index < musicNames.size() - 1) {
                    System.out.println("\n➡️  Próxima música!");
                    index++;
                } else if (lastSong){
                    index++;
                } else {
                    System.out.println("\n🚫 Já estás na última música.");
                }
            } else if (result == PlayerAction.PREVIOUS) {
                if (index > 0) {
                    System.out.println("\n⬅️  Música anterior!");
                    index--;
                } else {
                    System.out.println("\n🚫 Já estás na primeira música.");
                }
            } else if (result == PlayerAction.QUIT) {
                System.out.println("\n⏹️  Parar reprodução.");
                running = false;
            }
        }
        System.out.println("\n✅ Fim da reprodução! Obrigado por ouvir \uD83C\uDFB6\uD83C\uDFB5\n");
    }

    /**
     * Reproduz uma música em modo áudio.
     * Mostra barra de progresso e permite comandos do utilizador.
     * @param mf Informações da música
     * @param musicName Nome da música
     * @return PlayerAction ação escolhida pelo utilizador
     */
    private PlayerAction playAudioSingle(MusicInfo mf, String musicName) {
        MusicPlayer mp = new MusicPlayer();
        try {
            Clip clip = mp.playMusic(musicName);
            if (clip == null) {
                System.out.println("\n🚫 Arquivo de áudio não encontrado.");
                return PlayerAction.NONE;
            }
            System.out.println("\n\uD83C\uDFB5🎧 Agora a tocar: " + mf.getMusicName() + " - " + mf.getArtistName() + "\n");
            long total = clip.getMicrosecondLength();
            while (clip.isRunning()) {
                Thread.sleep(200);
                printBar(total / 1000, clip.getMicrosecondPosition() / 1000);
                if (System.in.available() > 0) {
                    String command = getScanner().nextLine().trim().toLowerCase();
                    PlayerAction input = handleCommand(command);
                    if (input != PlayerAction.NONE) {
                        clip.stop();
                        return input;
                    }
                }
            }
            clip.close();
            return PlayerAction.NEXT; // Avança para próxima por defeito
        } catch (Exception e) {
            System.out.println("\n❌ Erro ao reproduzir: " + e.getMessage());
            return PlayerAction.NONE;
        }
    }

    /**
     * Reproduz a letra de uma música, palavra a palavra, permitindo comandos.
     * Se existir URL, abre no navegador.
     * @param mf Informações da música
     * @return PlayerAction ação escolhida pelo utilizador
     */
    private PlayerAction playLyricsSingle(MusicInfo mf) {
        try {
            if (!mf.getErrorMessage().isEmpty()) {
                System.out.println("\n🚫 " + mf.getErrorMessage());
                return PlayerAction.NEXT;
            }
            if (!mf.getUrl().isEmpty()) {
                System.out.println("\n🌐 A abrir o link da música no navegador...");
                BrowserOpener browserOpener = new BrowserOpener(mf.getUrl());
                browserOpener.abrir();
            }
            if (mf.isExplicit()) {
                System.out.println("\n🔞 Música com conteúdo explícito.");
            }
            System.out.println("\n\uD83C\uDFB6 Letra de: " + mf.getMusicName() + " - " + mf.getArtistName());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            for (String word : mf.getLyrics()) {
                System.out.print(word + " ");
                Thread.sleep(500);
                if (System.in.available() > 0) {
                    String command = getScanner().nextLine().trim().toLowerCase();
                    PlayerAction input = handleCommand(command);
                    if (input != PlayerAction.NONE) {
                        System.out.println();
                        return input;
                    }
                }
            }
            System.out.println();
            return PlayerAction.NEXT; // Avança para próxima por defeito
        } catch (Exception e) {
            System.out.println("\n❌ Erro ao reproduzir: " + e.getMessage());
            return PlayerAction.NEXT;
        }
    }

    /**
     * Interpreta comandos do utilizador durante a reprodução.
     * @param command Comando inserido
     * @return PlayerAction correspondente
     */
    private PlayerAction handleCommand(String command) {
        if (command.equals("f") && getController().canCurrentUserSkip())
            return PlayerAction.NEXT;
        else if (command.equals("b") && getController().canCurrentUserSkip())
            return PlayerAction.PREVIOUS;
        else if (command.equals("q"))
            return PlayerAction.QUIT;
        return PlayerAction.NONE;
    }

    /**
     * Mostra uma barra de progresso da música.
     * @param total Duração total
     * @param current Posição atual
     */
    private void printBar(long total, long current) {
        int barLength = 50;
        int progress = (int) ((current * barLength) / total);
        StringBuilder bar = new StringBuilder("\r|");
        for (int i = 0; i < barLength; i++) {
            if (i < progress) bar.append("█");
            else bar.append("░");
        }
        bar.append("| ");
        long elapsedTime = current / 1000;
        long totalTime = total / 1000;
        int percentage = (int) ((current * 100) / total);
        bar.append(String.format("%02d:%02d / %02d:%02d (%d%%)",
                elapsedTime / 60, elapsedTime % 60,
                totalTime / 60, totalTime % 60,
                percentage));
        System.out.print(bar.toString());
        System.out.flush();
    }

}
