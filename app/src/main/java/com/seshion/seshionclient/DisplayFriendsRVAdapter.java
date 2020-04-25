package com.seshion.seshionclient;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.security.PublicKey;
import java.util.List;

import javax.crypto.Cipher;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DisplayFriendsRVAdapter extends RecyclerView.Adapter<FriendsRVHolder> {

    private static Socket socket;
    private static DataOutputStream dataBufferedNetOutputStream;/* data stream to send/receive byte arrays */
    private static DataInputStream dataBufferedNetInputStream;
    private static DataInputStream dataNetInputStream;//no buffer
    private static DataOutputStream dataNetOutputStream;//no buffer
    private static BufferedOutputStream bufferedOutputStream;/* buffered streams adds memory, and makes stream communication asynchronous. */
    private static BufferedInputStream bufferedInputStream;

    private static String SERVER_IP = "134.209.38.86";
    private static AES aes;


    Context context;
    List<UserAccount> userAccounts; //this array list creates a list of array which parameters define in our model class

    public DisplayFriendsRVAdapter(Context context, List<UserAccount> userAccounts) {
        this.context = context;
        this.userAccounts = userAccounts;
    }

    @NonNull
    @Override
    public FriendsRVHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friends_row, null); // this line inflates
        // our friends_row



        return new FriendsRVHolder(view);// this will return our view to holder class
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsRVHolder holder, int position) {
        final int i = position;
        holder.userNameTextView.setText(userAccounts.get(i).getUserName()); // set the username
        //holder.currentSeshionTextView.setText(userAccounts.get(i).getJoinedSessions().get(0).getName());
        holder.currentSeshionTextView.setText(userAccounts.get(i).getUserName());
        //holder.imageView.setImageResource(userAccounts.get(i).getImage()); // here we used image resource
        // because we will use images in our resource folder which is drawable

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendFriendRequest sendFriendRequest = new SendFriendRequest();
                sendFriendRequest.execute();

                Toast.makeText(v.getContext(), userAccounts.get(i).getUserName() + "", Toast.LENGTH_SHORT).show();

//                AddFriend addFriend = new AddFriend();
//                addFriend.execute("Twizzy", "NickIsTheMan");
            }
        });

    }

    @Override
    public int getItemCount() {
        return userAccounts.size();
    }


    class SendFriendRequest extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;


        @Override
        protected String doInBackground(String... homies) {



            /* response from server */
            List<UserAccount> returnedFriends = null;

            /* print seshion info */
            //System.out.println(homies[0] + " is looking for " + homies[1]);

            String response = "";

            /* connect to server with the specified SERVER_IP address and port (socket obj is client socket) */
            try {
                socket = new Socket(SERVER_IP, 8090);

                /* streams */
                bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
                dataBufferedNetOutputStream = new DataOutputStream(bufferedOutputStream);
                dataNetOutputStream = new DataOutputStream(socket.getOutputStream());

                bufferedInputStream = new BufferedInputStream(socket.getInputStream());
                dataBufferedNetInputStream = new DataInputStream(bufferedInputStream);
                dataNetInputStream = new DataInputStream(socket.getInputStream());

                /* obtain public RSA key from server */
                System.out.println("attempting to obtain key length...");

                /* obtain AES (symmetric) key information from client */
                int publicKeyLength = dataNetInputStream.readInt();
                System.out.println("public key length:" + publicKeyLength);
                byte[] keyBytes = new byte[publicKeyLength];
                System.out.println("attempting to read ");
                dataNetInputStream.readFully(keyBytes, 0, publicKeyLength);
                System.out.println("Received public RSA key from server");

                /* recover the key (decode it, public uses X.509, private uses PKCS8 */
                PublicKey rsaPublicKey = null;
                Cipher asymmetricCipher = null;
                RSA rsa = new RSA();
                //RSA rsa = RSA.getInstance();
                rsaPublicKey = rsa.decodePublicKey(keyBytes);
                rsa.setPublicKey(rsaPublicKey);
                System.out.println("Public Key Decoded: \n" + rsaPublicKey.toString());

                /* AES symmetric encryption key */
                aes = new AES();
                //AES aes = AES.getInstance();
                byte[] aesKey = aes.getSymmetricKey();
                byte[] encryptedAESKey = null;

                /* encrypt symmetric (AES) key with RSA and send to server */
                encryptedAESKey = rsa.encrypt(aesKey);
                System.out.println("RSA Encrypted AES key" + encryptedAESKey);
                System.out.println("RSA Encrypted AES key size" + encryptedAESKey.length);
                dataNetOutputStream.write(encryptedAESKey);//sends 16 byte key, may need the offset and length method

                /* connection and encryption done */

//                /* make json string using googles Gson library */
//                Gson gson = new Gson();
//                String json = gson.toJson(users[0]);//pass first and only user obj passed
//                System.out.println("json:\n" + json);
//
                /* get search for a friend json*/
                String json = Actions.addFriend(homies[0], homies[1]);

                /* make byte array */
                byte[] createSeshionBytes = json.getBytes();

                /* encrypt the data */
                byte[] encryptedByteArray = aes.encrypt(createSeshionBytes);

                /* now wed send the encryptedByteArray over the dataBufferedNetInputStream.
                 * First we'll send the length of the byte array we're sending,
                 * then the data.*/
                dataNetOutputStream.writeInt(encryptedByteArray.length);
                dataNetOutputStream.write(encryptedByteArray);

                /* receive response from server */
                int responseSize = dataNetInputStream.readInt();
                System.out.println("Receieved length: " + responseSize);
                byte[] responseArray = new byte[responseSize];
                dataNetInputStream.readFully(responseArray, 0, responseSize);
                System.out.println("Receieved responseArray: " + responseArray.toString());

                /* now to decrypt the message */
                byte[] decryptedMessage = aes.decrypt(responseArray);

                /* now we have to separate the response */
                response = new String(decryptedMessage);
                System.out.println("response:" + response);

                /* display the response */
                //returnedFriends = Actions.fetchFriends(response);




            } catch (EOFException eofe) {
                /* print stack trace */
                eofe.printStackTrace();

            } catch (IOException ioe) {
                /* print stack trace */
                ioe.printStackTrace();
            } finally {

                try {

                    /* try to flush the bufferedOutputStream */
                    bufferedOutputStream.flush();
                    bufferedOutputStream.close();

                    /* flush and close the dataBufferedNetOutputStream */
                    dataNetInputStream.close();
                    dataBufferedNetOutputStream.flush();
                    dataBufferedNetOutputStream.close();

                    /* disconnect the client */
                    socket.close();
                } catch (EOFException eofe) {
                    /* print stack trace */
                    eofe.printStackTrace();

                } catch (IOException ioe) {
                    /* print stack trace */
                    ioe.printStackTrace();

                }

            }

            return response;
        }

        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(
                    context,
                    "Sending request...",
                    "Please Wait...");
        }

        @Override
        protected void onPostExecute(String response){

            System.out.println("Response:" + response);
            if(response.equals("[\"0\"])")) {
                /* make a label that says no friends */

            } else {
                Toast.makeText(context, "Friend Request Sent!", Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(this, AddFriend.class);
//                intent.putExtra("USER_NAME", "Twizzy");
//                startActivity(intent);

            }

            /* dismiss the alert */
            progressDialog.dismiss();

            /* show little message to the user */

        }

    }
}
