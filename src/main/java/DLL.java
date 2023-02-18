import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

class DLL implements Runnable {

    static class Node {
        URL url;
        String songName;
        String songDirector;
        String language;
        Node prev;
        Node next;

        Node(URL url, String songName, String songDirector, String language) {
            this.url = url;
            this.songName = songName;
            this.songDirector = songDirector;
            this.language = language;
        }
    }


    boolean temp = true;
    int length = 0;
    Node toPlay;
    Node head;
    Clip clip;
    String curState = "paused";
    long totalDur;

    DLL() throws LineUnavailableException {
        clip = AudioSystem.getClip();
    }

    public void run() {
        try {
            play(toPlay);
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }

    void push(URL url, String songName, String songDirector, String language) {
        length = length + 1;
        Node new_Node = new Node(url, songName, songDirector, language);

        new_Node.next = null;
        new_Node.prev = null;
        if (head == null) {
            head = new_Node;
        } else {
            Node tempNode = head;
            while (tempNode.next != null) {
                tempNode = tempNode.next;
            }
            new_Node.prev = tempNode;
            tempNode.next = new_Node;
        }
    }

    Node playQueue(Node node) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        URL u1 = node.url;
        AudioInputStream ais = AudioSystem.getAudioInputStream(u1);
        clip.close();
        clip.open(ais);
        clip.start();
        totalDur = clip.getMicrosecondLength();
        curState = "playing";
        toPlay = node;
        return node;
    }

    void play(Node headQ) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        if (headQ == null)
            headQ = head;
        Node curNode = playQueue(headQ);
        while (true) {
            if (!clip.isActive() && curNode.next != null && ((totalDur - clip.getMicrosecondPosition()) == 0)) {
                curNode = playQueue(curNode.next);
            }
            if (!clip.isActive() && curNode.next == null && ((totalDur - clip.getMicrosecondPosition()) == 0)) {
                stopQueue();
                break;
            }
        }
    }

    void stopQueue() {
        curState = "paused";
        clip.stop();
        clip.close();
    }

    void resumeQueue() {
        curState = "playing";
        clip.start();
    }

    void pauseQueue() {
        curState = "paused";
        clip.stop();
    }

    float getVolume() {
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        return (float) Math.pow(10f, gainControl.getValue() / 20f);
    }

    void setVolume(float volume) {
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(volume));
    }

    void nextSong() {
        if (toPlay.next != null) {
            stopQueue();
            toPlay = toPlay.next;
        }
    }

    void previousSong() {
        if (toPlay.prev != null) {
            stopQueue();
            toPlay = toPlay.prev;
        }
    }

    void printQueue() {
        Node node = head;
        while (node != null) {
            System.out.print(node.songName + " | ");
            node = node.next;
        }
        System.out.println();
    }

}