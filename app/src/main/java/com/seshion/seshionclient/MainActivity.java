package com.seshion.seshionclient;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.security.PublicKey;

import javax.crypto.Cipher;

public class MainActivity extends AppCompatActivity {
    /* constants */
    private int REQUEST_LOCATION = 1;
    private static String SERVER_IP = "134.209.38.86";
    private static AES aes;

    /* vars */
    private static StateInfo stateInfo;
    private static Socket socket;
    private static DataOutputStream dataBufferedNetOutputStream;/* data stream to send/receive byte arrays */
    private static DataInputStream dataBufferedNetInputStream;
    private static DataInputStream dataNetInputStream;//no buffer
    private static DataOutputStream dataNetOutputStream;//no buffer
    private static BufferedOutputStream bufferedOutputStream;/* buffered streams adds memory, and makes stream communication asynchronous. */
    private static BufferedInputStream bufferedInputStream;
    private Context context;

    private TextView commsTextView;
    String message = "";

    /* used for displaying messages to the user regarding creating users and login */
    private TextView loginMessageTextView;

    /* THE USER - all data fields set after the login function */
    private UserAccount loggedInUser;

    //private String loginMessageFromMainActivity = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        /* controls */
        final EditText userEditText = (EditText) findViewById(R.id.useredittext);
        final EditText passEditText = (EditText) findViewById(R.id.passwordEditText);
        final TextView createUserTextView = (TextView) findViewById(R.id.createAccountLabel);
        final Button buttonLogin = (Button) findViewById(R.id.loginButton);
        loginMessageTextView = (TextView) findViewById(R.id.loginmessagetextview);

        /* instantiate the singleton connection and encryption objects */

        /* events */
        createUserTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /* make new UserAccount object */
                loggedInUser = new UserAccount(userEditText.getText().toString(), passEditText.getText().toString());

                /* make new task and execute (calls doInBackground and next unnecessary method from task )*/
                CreateUser cu = new CreateUser();
                cu.execute(loggedInUser);

                /* show little message to the user */
                Toast.makeText(getApplicationContext(), "Data sent", Toast.LENGTH_LONG).show();


            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /* prepare data */
                stateInfo = new StateInfo();

                /* make new UserAccount object */
                loggedInUser = new UserAccount(userEditText.getText().toString(), passEditText.getText().toString());

                /* try to login in new thread*/
                Login login = new Login();
                login.execute(loggedInUser);

                /* show little message to the user */
                Toast.makeText(getApplicationContext(), "Data sent", Toast.LENGTH_LONG).show();

            }
        });
    }

    public void launchDashboard(UserAccount user){
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra("UserAccount", user);
        startActivity(intent);

    }

    public void launchDialogFrag(){

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        MyDialogFragment testDialog = MyDialogFragment.newInstance(1, 2, "You just checked into a seshion!");
        testDialog.show(ft, "dialog");

    }

    public void launchDashboard(String response){
        Intent intent = new Intent(this, DashboardActivity.class);

        intent.putExtra("Response", response);
        intent.putExtra("UserAccount", loggedInUser);
        startActivity(intent);

    }


    class Login extends AsyncTask<UserAccount, Void, String>
    {
        private String resp;
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(UserAccount... users) {
            try {
                /* looper for creating a handler inside a thread */
                Looper.prepare();

                /* print credentials */
                System.out.println("credentials:" + users[0].getUserName() + "pass:" + users[0].getPassword());

                /* connect to server with the specified SERVER_IP address and port (socket obj is client socket)*/
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
//                dataBufferedNetInputStream.readFully(keyBytes);
//                dataBufferedNetInputStream.readFully(keyBytes, 0, publicKeyLength);
//                dataBufferedNetInputStream.read(keyBytes);
//                dataBufferedNetInputStream.read(keyBytes, 0, publicKeyLength);

//                dataNetInputStream.readFully(keyBytes);
                dataNetInputStream.readFully(keyBytes, 0, publicKeyLength);
//                dataNetInputStream.read(keyBytes);
//                dataNetInputStream.read(keyBytes, 0, publicKeyLength);
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

                /* make login json */
                String json = Actions.makeLoginJson(users[0]);

                /* make byte array */
                byte[] credentialsBytes = json.getBytes();

                /* encrypt the data */
                byte[] encryptedByteArray = aes.encrypt(credentialsBytes);

                /* now we send the encryptedByteArray over the dataBufferedNetInputStream.
                 * First we'll send the length of the byte array we're sending,
                 * then the data.*/
                dataNetOutputStream.writeInt(encryptedByteArray.length);
                dataNetOutputStream.write(encryptedByteArray);


                /* SENT LOGIN JSON INFORMATION, AWAITING RESPONSE */


                /* receive response from server */
                int responseSize = dataNetInputStream.readInt();
                System.out.println("Receieved length: " + responseSize);
                byte[] responseArray = new byte[responseSize];
                dataNetInputStream.readFully(responseArray, 0, responseSize);
                System.out.println("Receieved responseArray: " + responseArray.toString());

                /* now to decrypt the message */
                byte[] decryptedMessage = aes.decrypt(responseArray);

                /* now we have to separate the response */
                String response = new String(decryptedMessage);
                System.out.println("response:" + response);

                /* set the loggedInUser Object with all of the data */
                //Actions.packStateInfo(stateInfo, response, loggedInUser);
                //Actions.packUserInfo(response, loggedInUser);

//                Gson gson = new Gson();
//                JsonParser parser = new JsonParser();
//                JsonArray array = parser.parse(response).getAsJsonArray();
//                System.out.println("array created");
//                String action = gson.fromJson(array.get(0), String.class);
//                List<UserAccount> friends = gson.fromJson(array.get(1), List.class);
//                List<UserGroup> groups = gson.fromJson(array.get(2), List.class);
//                List<UserGroup> joinedGroups = gson.fromJson(array.get(3), List.class);
//                List<Message> messages = gson.fromJson(array.get(4), List.class);
//                List<UserSession> ownedSeshions = gson.fromJson(array.get(5), List.class);
//                List<UserSession> invitedSeshions = gson.fromJson(array.get(6), List.class);
//                List<UserSession> joinedSeshions = gson.fromJson(array.get(7), List.class);
//                List<UserSession> allOpenSeshions = gson.fromJson(array.get(8), List.class);
//                for( int i = 0; i < friends.size(); i++){
//                    System.out.println("friend num" + i + "=" + friends.get(i));
//                }
//                for( int i = 0; i < groups.size(); i++){
//                    System.out.println("groups num" + i + "=" + groups.get(i));
//                }
//                for( int i = 0; i < joinedGroups.size(); i++){
//                    System.out.println("joinedGroups num" + i + "=" + joinedGroups.get(i));
//                }
//                for( int i = 0; i < messages.size(); i++){
//                    System.out.println("messages num" + i + "=" + messages.get(i));
//                }
//                for( int i = 0; i < ownedSeshions.size(); i++){
//                    System.out.println("ownedSeshions num" + i + "=" + ownedSeshions.get(i));
//                }
//                for( int i = 0; i < invitedSeshions.size(); i++){
//                    System.out.println("invitedSeshions num" + i + "=" + invitedSeshions.get(i));
//                }
//                for( int i = 0; i < joinedSeshions.size(); i++){
//                    System.out.println("joinedSeshions num" + i + "=" + joinedSeshions.get(i));
//                }
//                for( int i = 0; i < allOpenSeshions.size(); i++){
//                    System.out.println("allOpenSeshions num" + i + "=" + allOpenSeshions.get(i));
//                }
                /* set logged in user data */

                /* check if login credentials are verified and accepted */
                System.out.println("result:\n" + response);

                boolean doWeBelong = Actions.checkLoginResponse(response, loginMessageTextView);
                System.out.println("Login credentials valid?:" + doWeBelong);
                if( doWeBelong==true ){

                    /* switch activities to the main dashboard, and pass the user object */
                    launchDashboard(response);

                }

                /* try to flush the bufferedOutputStream */
                bufferedOutputStream.flush();
                bufferedOutputStream.close();

                /* flush and close the dataBufferedNetOutputStream */
                dataNetInputStream.close();
                dataBufferedNetOutputStream.flush();
                dataBufferedNetOutputStream.close();

                /* disconnect the client */
                socket.close();


                return response;

            } catch (EOFException eofe){
                /* print stack trace */
                eofe.printStackTrace();

            } catch(IOException ioe){
                /* print stack trace */
                ioe.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(
                    MainActivity.this,
                    "Checking Credentials...",
                    "Please Wait...");
        }

        @Override
        protected void onPostExecute(String result){
            progressDialog.dismiss();


        }


    }

    class CreateUser extends AsyncTask<UserAccount, Void, String>
    {

        @Override
        protected String doInBackground(UserAccount... users)
        {

            try {
                /* print credentials */
                System.out.println("credentials:" + users[0].getUserName() + "pass:" + users[0].getPassword());

                /* connect to server with the specified SERVER_IP address and port (socket obj is client socket)*/
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
//                dataBufferedNetInputStream.readFully(keyBytes);
//                dataBufferedNetInputStream.readFully(keyBytes, 0, publicKeyLength);
//                dataBufferedNetInputStream.read(keyBytes);
//                dataBufferedNetInputStream.read(keyBytes, 0, publicKeyLength);

//                dataNetInputStream.readFully(keyBytes);
                dataNetInputStream.readFully(keyBytes, 0, publicKeyLength);
//                dataNetInputStream.read(keyBytes);
//                dataNetInputStream.read(keyBytes, 0, publicKeyLength);
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

                /* make json string using googles Gson library */
                Gson gson = new Gson();
                //String json = gson.toJson(users[0]);//pass first and only user obj passed
                //System.out.println("json:\n" + json);

                /* add the action to the json */
                //json = addActionToJson("createUser", "user", json);
                //String json = makeTestJson("logout");
                String json = Actions.createNewUserJson(users[0].getUserName(), users[0].getPassword());
                System.out.println("json:\n" + json);

                /* make byte array */
                byte[] credentialsBytes = json.getBytes();

                /* encrypt the data */
                System.out.println("encrypting create user data");
                byte[] encryptedByteArray = aes.encrypt(credentialsBytes);

                /* now we'll send the encryptedByteArray over the dataBufferedNetInputStream.
                 * First we'll send the length of the byte array we're sending,
                 * then the data.*/
                System.out.println("sending length of encrypted json data");
                dataNetOutputStream.writeInt(encryptedByteArray.length);
                System.out.println("sending encrypted json data");
                dataNetOutputStream.write(encryptedByteArray);

//                /* print to console for testing */
//                String test = new String(encryptedByteArray);
//                System.out.println("encrypted text:" + test);
//
//                /* now to decrypt the message */
//                byte[] decryptedMessage = aes.decrypt(encryptedByteArray);
//                String originalMessage = new String(decryptedMessage);
//                System.out.println("original message:" + originalMessage);

                /* receive response from server */
                int responseSize = dataBufferedNetInputStream.readInt();
                byte[] responseArray = new byte[responseSize];
                dataBufferedNetInputStream.read(responseArray);

                /* now to decrypt the message */
                byte[] decryptedMessage = aes.decrypt(responseArray);
                String response = new String(decryptedMessage);
                System.out.println("response:" + response);

                /* try to flush the bufferedOutputStream */
                bufferedOutputStream.flush();
                bufferedOutputStream.close();

                /* flush and close the dataBufferedNetOutputStream */
                dataNetInputStream.close();
                dataBufferedNetOutputStream.flush();
                dataBufferedNetOutputStream.close();

                /* disconnect the client */
                socket.close();

                return response;

            } catch (EOFException eofe){
                /* print stack trace */
                eofe.printStackTrace();

            } catch(IOException ioe){
                /* print stack trace */
                ioe.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String result){

            /* login user by sending login information over to the server */
            //boolean weGood = Actions.checkLoginResponse(result, loginMessageTextView);
            /* try to login in new thread*/

            boolean weGood = Actions.checkCreateUserResponse(result, loginMessageTextView);
            if(weGood) {
                Login login = new Login();
                login.execute(loggedInUser);
                //launchDashboard(loggedInUser);
            }
        }
    }





}
