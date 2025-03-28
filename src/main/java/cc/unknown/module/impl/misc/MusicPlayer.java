package cc.unknown.module.impl.misc;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import cc.unknown.event.Kisoji;
import cc.unknown.event.Priority;
import cc.unknown.event.impl.buz.Listener;
import cc.unknown.event.impl.forge.ClientTickForgeEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.RadioPlayer;
import cc.unknown.util.render.ChatUtil;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.structure.lists.SList;
import cc.unknown.util.value.impl.ModeValue;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

@ModuleInfo(name = "MusicPlayer", category = Category.MISC)
public class MusicPlayer extends Module {
	
	private final ModeValue mode = new ModeValue("Mode", this, "Local", "Rock", "Phonk", "Trap", "NCS", "Party", "Classic", "NightCore", "Other", "90s", "Local");

	private boolean started = false;
	   
	private SList<File> musicFiles;
	private volatile AdvancedPlayer player;
	private final RadioPlayer radio = new RadioPlayer();
	
    @Override
    public void onEnable() {
    	started = true;
    }

    @Override
    public void onDisable() {   
        stopMusic();
        stopLocal();
        started = false;
    }
    @Kisoji(value = Priority.HIGHEST)
    public final Listener<ClientTickForgeEvent> onTick = event -> {
    	if (event.isPost()) return;

        if (started) {
        	if (mode.is("Local")) {
        	    String userHome = System.getProperty("user.home");
        	    String oneDrivePath = userHome + File.separator + "OneDrive" + File.separator + "Music";
        	    String defaultMusicPath = userHome + File.separator + "Music";

        	    File oneDriveMusic = new File(oneDrivePath);
        	    String musicFolderPath = oneDriveMusic.exists() ? oneDrivePath : defaultMusicPath;

        	    loadMusicFiles(musicFolderPath);
                if (musicFiles != null && !musicFiles.isEmpty()) {
                    playSong();
                } else {
                	setMessage("No local music files found.");
                }
            } else {
            	setMessage("Playing " + mode.getMode() + " playlist.");
                playOtherMusic(mode.getMode());
            }
            started = false;
        }
    };
    
    private void playLocal(File musicFile) {
    	Runnable musicRunnable = () -> {
    		try (FileInputStream fis = new FileInputStream(musicFile)) {
                player = new AdvancedPlayer(fis);

                player.setPlayBackListener(new PlaybackListener() {
                    @Override
                    public void playbackFinished(PlaybackEvent evt) {
                        playSong();
                    }
                });
  
                player.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        new Thread(musicRunnable).start();
    }

    private void playOtherMusic(String mode) {
        Runnable musicRunnable = () -> {
            if (radio != null) {
                radio.stop();
            }
            
            switch (mode) {
            case "Trap":
            	connectToMusic("trap");
            	break;
            case "Phonk":
            	connectToMusic("phonk");
            	break;
            case "Rock":
            	connectToMusic("radio-barbarossa");
            	break;
            case "NCS":
            	connectToMusic("ncsradio");
            	break;
            case "NightCore":
            	connectToMusic("nightcoremusic");
            	break;
            case "Party":
            	connectToMusic("djzubi");
            	break;
            case "90s":
            	connectToMusic("eurobeat");
            	break;
            case "Classic":
            	connectToMusic("classics");
            	break;
            case "Other":
            	connectToMusic("estacionmix");
            	break;
            }
        };

        new Thread(musicRunnable).start();
    }
    
    private void connectToMusic(String instruction) {
        radio.start("https://stream.laut.fm/" + instruction);
    }

    private void loadMusicFiles(String directoryPath) {
        File directory = new File(directoryPath);
        if (directory.exists() && directory.isDirectory()) {
            List<String> validExtensions = Arrays.asList(".mp3");
            musicFiles = new SList<>(Arrays.asList(directory.listFiles((dir, name) -> {
                for (String ext : validExtensions) {
                    if (name.endsWith(ext)) {
                        return true;
                    }
                }
                return false;
            })));
        }
    }

    private void playSong() {
        if (musicFiles != null && !musicFiles.isEmpty()) {
            File currentFile = musicFiles.fastRemoveFile(new Random().nextInt(musicFiles.size()));
            String fileName = currentFile.getName();
            String songName = fileName.replace(".mp3", "");
            
            songName = songName.substring(0, 1).toUpperCase() + songName.substring(1).toLowerCase();

            setMessage("Playing " + songName);
            playLocal(currentFile);
        } else {
            setMessage("No more songs to play.");
        }
    }
    
    private void stopMusic() {
    	Runnable musicRunnable = () -> {
            if (radio != null) {
                radio.stop();
            }
        };

        new Thread(musicRunnable).start();
    }
    
    private void stopLocal() {
        if (player != null) {
            player.close();
            player = null;
        }
    }
    
    private void setMessage(String message) {
    	ChatUtil.display(ColorUtil.pink + "[S]" + " " + ColorUtil.red + message);
    }
}