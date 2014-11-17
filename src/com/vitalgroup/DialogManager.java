package com.vitalgroup;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

interface OnPositiveListener{
	void OnPositive();
}

interface OnNegativeListener{
	void OnNegative();
}

public class DialogManager {
	private Context c;
	private String dmTitle = "Title";
	private String dmMessage = "";
	private AlertDialog alertDialog;
	private AlertDialog.Builder dialog;
	@SuppressWarnings("unused")
	private String yes = "Yes";
	@SuppressWarnings("unused")
	private String no = "No";
	@SuppressWarnings("unused")
	private Boolean two_btn = false;
	@SuppressWarnings("unused")
	private String ok = "OK";
	
	public DialogManager(Context _c){
		this.c = _c;
		alertDialog = new AlertDialog.Builder(this.c).create();
	}
	
	// set two button constructor
	public DialogManager(Context _c, Boolean _two_btn){
		this.c = _c;
		this.two_btn = _two_btn;
		dialog = new AlertDialog.Builder(this.c);
	}
	
	// set title
	public DialogManager setTitle(String _dmTitle){
		this.dmTitle = _dmTitle;
		return this;
	}
	
	// set message
	public DialogManager setMessage(String _dmMessage){
		this.dmMessage = _dmMessage;
		return this;
	}
	
	@SuppressWarnings("deprecation")
	public void alert(){
		
		alertDialog.setButton("OK", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which){
				alertDialog.cancel();
			}
		});
		this.showDialog();
	}
	
	// set one button
	public void setOneButton(String ok, final OnPositiveListener pos_listener){
		dialog.setPositiveButton(ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				pos_listener.OnPositive();
			}
		});
		
		showBtnsDialog();
	}
	
	// set to buttons
	public void setTwoButton(String yes,  final OnPositiveListener pos_listener, String no, final OnNegativeListener neg_listener){
		dialog.setPositiveButton(yes, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface _dialog, int which) {
				// TODO Auto-generated method stub
				pos_listener.OnPositive();
			}
		});
		
		dialog.setNegativeButton(no, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface _dialog, int which) {
				// TODO Auto-generated method stub
				neg_listener.OnNegative();
			}
		});
		
		showBtnsDialog();
	}
	
	// show dialog
	private void showDialog(){
		alertDialog.setTitle(this.dmTitle);
		alertDialog.setMessage(this.dmMessage);
		alertDialog.show();
	}
	
	private void showBtnsDialog(){
		dialog.setTitle(this.dmTitle);
		dialog.setMessage(this.dmMessage);
		dialog.create();
		dialog.show();
	}
}
