package com.example.meetapp;

import android.app.Dialog;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

class AddMembersHandler {
    private static Dialog addMembersDialog;
    private static ArrayList<String> membersToAdd = new ArrayList<>();

    static void setDialog(Dialog dialog){
               addMembersDialog = dialog;
    }

    static void onContactClick(View v){
        RelativeLayout contactLayout = v.findViewById(R.id.contactLayout);
        TextView contactName = v.findViewById(R.id.contactName);
        ImageButton okButton = addMembersDialog.findViewById(R.id.chooseMembers);
        if (contactLayout.getTag() != "chosen") {
            okButton.setBackgroundResource(R.drawable.green_round_background);
            contactLayout.setBackgroundColor(v.getResources().getColor(R.color.colorGreen));
            contactLayout.setTag("chosen");
             addMember((String) contactName.getText());
        }
        else{
             removeMember((String) contactName.getText());
             if (isMemberToAddEmpty()){
                 okButton.setBackgroundResource(R.drawable.disabled_button_background);
            }
            contactLayout.setBackgroundColor(v.getResources().getColor(R.color.colorWhite));
            contactLayout.setTag("notChosen");
        }
    }

    static void chooseMembers(final Runnable onChooseMembers){
        ImageButton okBtn = addMembersDialog.findViewById(R.id.chooseMembers);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMembersDialog.dismiss();
                onChooseMembers.run();
                AddMembersHandler.clearMembersToAdd();
            }
        });
    }

    private static void addMember(String member) {
        membersToAdd.add(member);
    }

    private static boolean isMemberToAddEmpty(){
        return membersToAdd.isEmpty();
    }

    private static void removeMember(String member){
        membersToAdd.remove(member);
    }

    static ArrayList<String> getMembersToAdd(){
        return membersToAdd;
    }

    static void clearMembersToAdd(){
        membersToAdd.clear();
    }
}
