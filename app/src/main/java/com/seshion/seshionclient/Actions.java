package com.seshion.seshionclient;

import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Actions {

    /* send user coordinates */
    public static void sendUserCoords(DataOutputStream dataNetOutputStream, AES aes, UserAccount loggedInUser) {
        /* make a json byte array containing the action: setusercoords and the user object containing them */
        Gson gson = new Gson();
        Collection col = new ArrayList();
                col.add("setusercoordinates");
                col.add(loggedInUser);
        String coordsJson = gson.toJson(col);
        byte[] coordsBytes = coordsJson.getBytes();
        byte[] encryptedCoordsBytes = aes.encrypt(coordsBytes);
        int encArraySize = encryptedCoordsBytes.length;


        try {
            /* first send the size of the array */
            dataNetOutputStream.writeInt(encArraySize);

            /* then send the array */
            dataNetOutputStream.write(encryptedCoordsBytes);

        } catch(IOException e) {
            e.printStackTrace();
        }

    }

    public static List<UserAccount> fetchFriends(String responseFromServer){
        List<UserAccount> fetchedFriends = null;

        /* explicit type to try and assign to from gson */
        Type listOfFriendType = new TypeToken<List<UserAccount>>() {}.getType();

        Gson gson = new Gson();
        JsonParser jParser = new JsonParser();
        JsonArray jArray = jParser.parse(responseFromServer).getAsJsonArray();
        fetchedFriends = gson.fromJson(jArray.get(1), listOfFriendType);

        return fetchedFriends;
    }

    /* search for friend */
    public static String searchForFriend(String friendsName){

        ArrayList<String> a = new ArrayList<String>();
        a.add("searchforfriend");
        //a.add(user);
        a.add(friendsName);

        Gson gson = new Gson();
        String json  = gson.toJson(a);
        return json;

    }

    /* add friend */
    public static String addFriend(String user, String friendsName){

        ArrayList<String> a = new ArrayList<String>();
        a.add("addfriend");
        a.add(user);
        a.add(friendsName);

        Gson gson = new Gson();
        String json  = gson.toJson(a);
        return json;

    }

    public static void packUserInfo(String response, UserAccount loggedInUser){

        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonArray array = parser.parse(response).getAsJsonArray();
        System.out.println("array created");
        List<UserAccount> usersFriends, friendRequests;
        List<UserGroup> ownedGroups = null, joinedGroups = null;
        List<Message> usersMessages = null;
        List<UserSession> ownedSeshions = null, invitedSeshions = null, joinedSeshions = null, allOpenSeshions = null;

        try{

            /* needed to obtain lists of parametarized types */
            Type listOfFriendType = new TypeToken<List<UserAccount>>() {}.getType();
            Type listOfGroupType = new TypeToken<List<UserGroup>>() {}.getType();
            Type listOfMessageType = new TypeToken<List<Message>>() {}.getType();
            Type listOfSeshionType = new TypeToken<List<UserSession>>() {}.getType();

            /* set all of the variables from the json response */
            usersFriends = gson.fromJson(array.get(1), listOfFriendType);
            friendRequests = gson.fromJson(array.get(2), listOfFriendType);
            ownedGroups = gson.fromJson(array.get(3), listOfGroupType);
            joinedGroups = gson.fromJson(array.get(4), listOfGroupType);
            usersMessages = gson.fromJson(array.get(5), listOfMessageType);
            ownedSeshions = gson.fromJson(array.get(6), listOfSeshionType);
            invitedSeshions = gson.fromJson(array.get(7), listOfSeshionType);
            joinedSeshions = gson.fromJson(array.get(8), listOfSeshionType);
            allOpenSeshions = gson.fromJson(array.get(9), listOfSeshionType);
            //System.out.println("response:" + rspns);
            for( int i = 0; i < usersFriends.size(); i++){
                System.out.println("friend num" + i + "=" + usersFriends.get(i));
            }
            for( int i = 0; i < ownedGroups.size(); i++){
                System.out.println("groups num" + i + "=" + ownedGroups.get(i));
            }
            for( int i = 0; i < joinedGroups.size(); i++){
                System.out.println("joinedGroups num" + i + "=" + joinedGroups.get(i));
            }
            for( int i = 0; i < usersMessages.size(); i++){
                System.out.println("messages num" + i + "=" + usersMessages.get(i));
            }
            for( int i = 0; i < ownedSeshions.size(); i++){
                System.out.println("ownedSeshions num" + i + "=" + ownedSeshions.get(i));
            }
            for( int i = 0; i < invitedSeshions.size(); i++){
                System.out.println("invitedSeshions num" + i + "=" + invitedSeshions.get(i));
            }
            for( int i = 0; i < joinedSeshions.size(); i++){
                System.out.println("joinedSeshions num" + i + "=" + joinedSeshions.get(i));
            }
            for( int i = 0; i < allOpenSeshions.size(); i++){
                System.out.println("allOpenSeshions num" + i + "=" + allOpenSeshions.get(i));
            }

//            stateInfo.setLoginResponse(rspns);
//            stateInfo.setUserAccount(loggedInUser);
//            stateInfo.setUsersFriends(usersFriends);
//            //stateInfo.setFriendRequests(friendRequests);
//            stateInfo.setOwnedFriendGroups(ownedGroups);
//            stateInfo.setJoinedFriendGroups(joinedGroups);
//            stateInfo.setMessages(usersMessages);
//            stateInfo.setOwnedSeshions(ownedSeshions);
//            stateInfo.setInvitedSeshions(invitedSeshions);
//            stateInfo.setJoinedSeshions(joinedSeshions);
//            stateInfo.setAllOpenSeshions(allOpenSeshions);


            loggedInUser.setAllFriends(usersFriends);
            loggedInUser.setFriendRequests(friendRequests);
            loggedInUser.setOwnedGroups(ownedGroups);
            //loggedInUser.setJoinedGroups(joinedGroups);
            loggedInUser.setMessages(usersMessages);
            //loggedInUser.setOwnedSessions(ownedSeshions);
            //loggedInUser.setInvitedSessions(invitedSeshions);
            loggedInUser.setJoinedSessions(joinedSeshions);
            loggedInUser.setAllOpenSessions(allOpenSeshions);

        }catch (NullPointerException npe){

            System.out.println("encountered null pointer trying to parse json" +
                    "response from server is bunk" + npe);
        }

    }


    public static void packStateInfo(StateInfo stateInfo, String response, UserAccount loggedInUser){

        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonArray array = parser.parse(response).getAsJsonArray();
        System.out.println("array created");
        String rspns;
        List<String> friendRequests = null, friendList = null;
        List<UserAccount> usersFriends;
        List<UserGroup> ownedGroups = null, joinedGroups = null;
        List<Message> usersMessages = null;
        List<UserSession> ownedSeshions = null, invitedSeshions = null, joinedSeshions = null, allOpenSeshions = null;

        try{

            /* set all of the variables from the json response */
            rspns = gson.fromJson(array.get(0), String.class);
            usersFriends = gson.fromJson(array.get(1), List.class);
            ownedGroups = gson.fromJson(array.get(2), List.class);
            joinedGroups = gson.fromJson(array.get(3), List.class);
            usersMessages = gson.fromJson(array.get(4), List.class);
            ownedSeshions = gson.fromJson(array.get(5), List.class);
            invitedSeshions = gson.fromJson(array.get(6), List.class);
            joinedSeshions = gson.fromJson(array.get(7), List.class);
            allOpenSeshions = gson.fromJson(array.get(8), List.class);
            System.out.println("response:" + rspns);
            for( int i = 0; i < usersFriends.size(); i++){
                System.out.println("friend num" + i + "=" + usersFriends.get(i));
            }
            for( int i = 0; i < ownedGroups.size(); i++){
                System.out.println("groups num" + i + "=" + ownedGroups.get(i));
            }
            for( int i = 0; i < joinedGroups.size(); i++){
                System.out.println("joinedGroups num" + i + "=" + joinedGroups.get(i));
            }
            for( int i = 0; i < usersMessages.size(); i++){
                System.out.println("messages num" + i + "=" + usersMessages.get(i));
            }
            for( int i = 0; i < ownedSeshions.size(); i++){
                System.out.println("ownedSeshions num" + i + "=" + ownedSeshions.get(i));
            }
            for( int i = 0; i < invitedSeshions.size(); i++){
                System.out.println("invitedSeshions num" + i + "=" + invitedSeshions.get(i));
            }
            for( int i = 0; i < joinedSeshions.size(); i++){
                System.out.println("joinedSeshions num" + i + "=" + joinedSeshions.get(i));
            }
            for( int i = 0; i < allOpenSeshions.size(); i++){
                System.out.println("allOpenSeshions num" + i + "=" + allOpenSeshions.get(i));
            }

            stateInfo.setLoginResponse(rspns);
            stateInfo.setUserAccount(loggedInUser);
            stateInfo.setUsersFriends(usersFriends);
            //stateInfo.setFriendRequests(friendRequests);
            stateInfo.setOwnedFriendGroups(ownedGroups);
            stateInfo.setJoinedFriendGroups(joinedGroups);
            stateInfo.setMessages(usersMessages);
            stateInfo.setOwnedSeshions(ownedSeshions);
            stateInfo.setInvitedSeshions(invitedSeshions);
            stateInfo.setJoinedSeshions(joinedSeshions);
            stateInfo.setAllOpenSeshions(allOpenSeshions);

        }catch (NullPointerException npe){

            System.out.println("encountered null pointer trying to parse json" +
                    "response from server is bunk" + npe);
        }

    }

    /* create user */
    public static String createNewUserJson(String username, String password) {
        /* make json string using googles Gson library */
        Gson gson = new Gson();

        /* createuser */
        UserAccount newUser = new UserAccount(username, password);

        Collection cuElements = new ArrayList();
        cuElements.add("createuser");
        cuElements.add(newUser);
        String createUser = gson.toJson(cuElements);

        return createUser;
    }

    /* create seshion */
    public static String createNewSeshionJson(UserSession userSession) {
        /* make json string using googles Gson library */
        Gson gson = new Gson();

        Collection csElements = new ArrayList();
        csElements.add("createseshion");
        csElements.add(userSession);
        String createSeshion = gson.toJson(csElements);

        System.out.println("createSeshion json:" + createSeshion);

        return createSeshion;
    }

    public static boolean checkCreateUserResponse(String response, TextView loginMessageTextView) {
        response = response.replace("\"", "");
        if (response.equals("0")) {
            System.out.println("This username has already been taken or it violates a constraint");
            loginMessageTextView.setText("This username has already been taken or it violates a constraint");
            return false;
        } else if (response.equals("-1")) {
            System.out.println("This username or password are under the minimum length of 6 characters");
            loginMessageTextView.setText("This username or password are under the minimum length of 6 characters");
            return false;
        }else if (response.equals("1"))
            return true;
        else {
            System.out.println("Error with response from server in create user");
            loginMessageTextView.setText("Error with response from server in create user");
            return false;
        }
    }

    /* login */
    public static String makeLoginJson(UserAccount tempUser) {
        /* make json string using googles Gson library */
        Gson gson = new Gson();

        /* checkcredentials */
        Collection ccElements = new ArrayList();
        ccElements.add("login");
        ccElements.add(tempUser);
        String checkCredentials = gson.toJson(ccElements);

        return checkCredentials;
    }

    public static boolean checkLoginResponse(String response, TextView loginMessageTextView) {

        if (response.startsWith("[\"0")) {
            System.out.println("Incorrect username / password");
            //loginMessageTextView.setText("Incorrect username / password");
            return false;
        }else if (response.startsWith("[\"1"))
            return true;
        else {
            System.out.println("Error with response from server in create user");
            //loginMessageTextView.setText("Error with response from server in create user");
            return false;
        }
    }

    public static UserSession fetchSeshionCheckIn(String response, UserSession theOneWedCheckInto) {

        if (response.startsWith("[\"0") || response.startsWith("[\"-1")  ) {
            System.out.println("Didn't check into any seshions");
            return null;
        }else if (response.startsWith("[\"1")) {
            System.out.println("Checked into a seshion!");

            /* fetch the user session from the response json */
            Type seshionType = new TypeToken<UserSession>(){}.getType();
            Gson gson = new Gson();
            JsonParser jsonParser = new JsonParser();
            JsonArray jsonElements = jsonParser.parse(response).getAsJsonArray();
            theOneWedCheckInto = gson.fromJson(jsonElements.get(1), seshionType);
            return theOneWedCheckInto;

        } else {
            System.out.println("Error with response from server in send coords");
            //loginMessageTextView.setText("Error with response from server in create user");
            return null;
        }
    }

//    public static String makeTestJson(String action) {
//        ArrayList<String> jsonTestArray = new ArrayList<String>();
//
//        UserAccount userObj = new UserAccount("TwizzyBomb", "test");
//        userObj.setLatitude(41.063289);
//        userObj.setLongitude(-73.546232);
//        UserAccount userObj2 = new UserAccount("DXDR69", "test");
//        UserAccount userObj3 = new UserAccount("ShenIsAnyone", "test");
//        UserAccount userObj4 = new UserAccount("IonizedSilver", "test");
//        UserAccount userObj5 = new UserAccount("DavidAttenborough", "test");
//        ArrayList<UserAccount> friendList = new ArrayList<UserAccount>();
//        friendList.add(userObj2);
//        friendList.add(userObj3);
//        friendList.add(userObj4);
//        friendList.add(userObj5);
//
//        /* user group */
//        UserGroup userGroup = new UserGroup("Basket Ball Homies", "TwizzyBomb");
//
////        /* User Session */
////        UserSession userSession = new UserSession("Skazi skate sesh", "TwizzyBomb",
////                41.063883, -73.550780,
////                41.063203,  -73.550610,
////                41.063336, -73.549538,
////                41.064032, -73.549704,
////                java.time.LocalDate.now(), LocalTime.NOON,
////                false );
//
////        /* make json string using googles Gson library */
////        Gson gson = new Gson();
////
////        /* createuser */
////        Collection cuElements = new ArrayList();
////        cuElements.add("createuser");
////        cuElements.add(userObj);
////        String createUser = gson.toJson(cuElements);
////
////        /* checkcredentials */
////        Collection ccElements = new ArrayList();
////        ccElements.add("login");
////        ccElements.add(userObj);
////        String checkCredentials = gson.toJson(ccElements);
////
////        /* logout */
////        Collection loElements = new ArrayList();
////        loElements.add("logout");
////        loElements.add(userObj);
////        String logout = gson.toJson(loElements);
////
////        /* setusercoords */
////        Collection sucElements = new ArrayList();
////        sucElements.add("setcoordinates");
////        sucElements.add(userObj);
////        String setUserCoords = gson.toJson(sucElements);
////
////        /* addfriend */
////        Collection afElements = new ArrayList();
////        afElements.add("addfriend");
////        afElements.add(userObj);
////        afElements.add(userObj2);
////        String addFriend = gson.toJson(afElements); //needs 2 user objects
////
////        /* createfriendgroup */
////        Collection cfgElements = new ArrayList();
////        cfgElements.add("createfriendgroup");
////        cfgElements.add(userObj);
////        cfgElements.add(friendList);
////        String createFriendGroup = gson.toJson(cfgElements);//needs a user object, group object,
////
////        /* createseshion */
////        Collection csElements = new ArrayList();
////        csElements.add("createseshion");
////        csElements.add(userObj);
////        csElements.add(userSession);
////        String createSeshion = gson.toJson(csElements);//user, arrayList<User> , session object
////
////        /* joinseshion */
////        Collection jsElements = new ArrayList();
////        jsElements.add("joinseshion");
////        jsElements.add(userObj);
////        jsElements.add(userSession);
////        String joinSeshion = gson.toJson(jsElements);//seshion object, user
////
////        /* leaveseshion */
////        Collection lsElements = new ArrayList();
////        lsElements.add("leaveseshion");
////        lsElements.add(userObj);
////        lsElements.add(userSession);
////        String leaveSeshion = gson.toJson(lsElements);//seshion object, user
////
////        /* deleteseshion */
////        Collection dsElements = new ArrayList();
////        dsElements.add("deleteseshion");
////        dsElements.add(userObj);
////        dsElements.add(userSession);
////        String deleteSeshion = gson.toJson(dsElements);//seshion
////
////        switch(action) {
////            case "createuser":
////                return createUser;
////            case "login":
////                return checkCredentials;
////            case "logout":
////                return logout;
////            case "setcoordinates":
////                return setUserCoords;
////            case "addfriend":
////                return addFriend;
//////            case "changeuservisibility":
//////                return changeUserVisibility;
////            case "createfriendgroup":
////                return createFriendGroup;
////            case "createseshion":
////                return createSeshion;
////            case "joinseshion":
////                return joinSeshion;
////            case "leaveseshion":
////                return leaveSeshion;
////            case "deleteseshion":
////                return deleteSeshion;
////            default:
////                return "nothing to test";
//
//        }
    //}

    //    public String addActionToJson(String action, String object, String currentJson){
//        StringBuilder sb = new StringBuilder();
//        sb.append("{\"action\": \"" + action + "\",");
//        sb.append("\"" + object + "\": " + currentJson + "}");
//
//        currentJson = sb.toString();
//        return currentJson;
//
//    }
}
