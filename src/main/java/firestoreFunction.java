import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class firestoreFunction {
    public static ArrayList<DocumentSnapshot> curQuery;

    Firestore db = Music_UI.dataBaseAuth.db;

    ArrayList<DocumentSnapshot> querySong(String searchQuery) throws ExecutionException, InterruptedException {
        searchQuery = searchQuery.toLowerCase(Locale.ROOT);        // Converting To Lower Case

        // Querying In All Possible Fields

        ApiFuture<QuerySnapshot> keyWordSnapshot =
                db.collection("songData").whereArrayContains("keyword", searchQuery).get();
        List<QueryDocumentSnapshot> keyWordDoc = keyWordSnapshot.get().getDocuments();              // Key-word

        ApiFuture<QuerySnapshot> languageSnapShot =
                db.collection("songData").whereEqualTo("Language", searchQuery).get();
        List<QueryDocumentSnapshot> languageDoc = languageSnapShot.get().getDocuments();            //  Language

        ApiFuture<QuerySnapshot> movieNameSnapShot =
                db.collection("songData").whereEqualTo("movieName", searchQuery).get();
        List<QueryDocumentSnapshot> movieNameDoc = movieNameSnapShot.get().getDocuments();          //  Movie Name

        ApiFuture<QuerySnapshot> musicDirectorSnapShot =
                db.collection("songData").whereEqualTo("musicDirector", searchQuery).get();
        List<QueryDocumentSnapshot> musicDirectorDoc = musicDirectorSnapShot.get().getDocuments();  // Music Director

        ApiFuture<QuerySnapshot> songNameSnapShot =
                db.collection("songData").whereEqualTo("songName", searchQuery).get();
        List<QueryDocumentSnapshot> songNameDoc = songNameSnapShot.get().getDocuments();            // Song Name

        ArrayList<DocumentSnapshot> documentTempReference = new ArrayList<>(keyWordDoc);
        documentTempReference.addAll(languageDoc);
        documentTempReference.addAll(movieNameDoc);
        documentTempReference.addAll(musicDirectorDoc);
        documentTempReference.addAll(songNameDoc);

        ArrayList<String> docReferenceTemp = new ArrayList<>();
        ArrayList<DocumentSnapshot> documentReferences = new ArrayList<>();

        for (DocumentSnapshot document : documentTempReference) {
            String docID = document.getId();
            if (!docReferenceTemp.contains(docID)) {
                docReferenceTemp.add(docID);
                documentReferences.add(document);
            }
        }

        curQuery = documentReferences;
        return documentReferences;
    }

    ArrayList<DocumentSnapshot> returnQuery() {
        return curQuery;
    }

    public String toTitleCase(String str) {
        if (str.length() == 1)
            return str.toUpperCase();

        String[] words = str.split(" ");

        StringBuilder sb = new StringBuilder(str.length());

        for (String word : words) {

            if (word.length() > 1)
                sb.append(word.substring(0, 1).toUpperCase()).append(word.substring(1).toLowerCase());
            else
                sb.append(word.toUpperCase());
            sb.append(" ");
        }

        return sb.toString().trim();
    }
}
