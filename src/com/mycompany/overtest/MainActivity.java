package com.mycompany.overtest;

import android.app.*;
import android.content.*;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.*;
import android.provider.MediaStore;
import android.text.Html;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.webkit.MimeTypeMap;
import android.webkit.WebChromeClient;
import android.widget.*;
import java.io.*;
import java.lang.Process;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends Activity {

    Actions actions = new Actions();
	//ListEditor le = new ListEditor();
    Intent globalService;
	Intent list;

    //screen params
    public int width;
    public int height;

    public int help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        help = 1;

        //init
        globalService = new Intent(this,GlobalTouchService.class);

        //create WF folder
        actions.CreateMainFolder();
		//create main files
		String[] def = new String[3];
		def[0] = "Expected result:";
		def[1] = "Actual result:";
		def[2] = "Error log:";

		actions.Settings("expected.txt", def);
		
		String[] deff = new String[1];
		deff[0] = "New bug report";
		actions.Settings("description.txt", deff);

        if (help == 1){
            Toast.makeText(this, "Press \"MENU\" - \"HELP\" button, to see the help page.", Toast.LENGTH_SHORT).show();
            help = 0;
        }
		else{}
	}

	//btn start
    public void buttonGetClicked(View start){
        try {
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.harad.sysrep")));
		} catch (android.content.ActivityNotFoundException anfe) {
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.harad.sysrep")));
		}
		
		
 	}

	//btn stop
	public void buttonShareClicked(View stop){
		Intent email = new Intent(Intent.ACTION_SEND_MULTIPLE);
		email.putExtra(Intent.EXTRA_EMAIL, new String[]{""});		  
		email.putExtra(Intent.EXTRA_SUBJECT, actions.getDescription(Environment.getExternalStorageDirectory() + "/MANUAL/settings/description.txt").toString());
		email.putExtra(Intent.EXTRA_TEXT, actions.getDescription(Environment.getExternalStorageDirectory() + "/MANUAL/workflow/test.txt").toString());
        //Toast.makeText(this, le.genEmail(), Toast.LENGTH_LONG).show();

//		email.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(actions.getDescription(Environment.getExternalStorageDirectory() + "/MANUAL/workflow/test.html")
//                .toString()
//                .replace("<img src='", "")
//                .replace("' width='340px' height='200px' />", "")
//                .replace("' width='200px' height='340px' />", "")
//				));

		File f = new File(Environment.getExternalStorageDirectory() + "/MANUAL/workflow");
		File file[] = f.listFiles();
		ArrayList<Uri> uris = new ArrayList<Uri>();
		for (int i=0; i < file.length; i++) {
			uris.add(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/MANUAL/workflow/" + file[i].getName())));
			//Toast.makeText(getApplicationContext(), Environment.getExternalStorageDirectory() + "/MANUAL/workflow/" + file[i].getName(), Toast.LENGTH_LONG).show();
		}

		email.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

		email.setType("text/html");
		startActivity(Intent.createChooser(email, "Choose an Email client :"));
   	}

	//btn empty
	public void buttonEmptyClicked(View empty){

		new AlertDialog.Builder(this)
			.setTitle("Empty workflow")
			.setMessage("All your files will be deleted!")
			.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) { 
					stopService(globalService);
					//todo
					File flow = new File(Environment.getExternalStorageDirectory() + "/MANUAL/workflow");
					deleteDirPng(flow);
					File sett = new File(Environment.getExternalStorageDirectory() + "/MANUAL/settings");
					deleteDirPng(sett);
				}
			})
			.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) { 
					// do nothing
				}
			})
			//.setIcon(android.R.drawable.ic_menu_delete)
			.show();
		
        
	}

	//btn edit
    public void buttonEditClicked(View edit){

        list = new Intent(this,ListEditor.class);
        startActivity(list);

    }

	//btn view
    public void buttonViewClicked(View view){

        //show = new Intent(this,Show.class);
        //startActivity(show);

        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        File file = new File(Environment.getExternalStorageDirectory() + "/MANUAL/workflow/test.html");
        intent.setDataAndType(Uri.fromFile(file), "text/html");
        startActivity(intent);

        //File file = new File(Environment.getExternalStorageDirectory().toString() + "/MANUAL/test.html");
        //Uri webPageUri = Uri.fromFile(file);

        //Intent intent = new Intent(Intent.ACTION_VIEW);
        //intent.setDataAndType(webPageUri, "text/html");
        //startActivity(intent);

    }

    private static boolean deleteDirPng(File dir) {

        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDirPng(new File(dir, children[i]));
                if (!success) {
                    Log.d("Files", "DELETE: FAIL");
                    return false;
                }
            }
        }
        return (dir.getName().contains("."))? dir.delete() : false;
    }
		
	private static boolean deleteDirTxt(File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDirTxt(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return (dir.getName().contains(".txt"))? dir.delete() : false;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mainmenu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.help:

                LayoutInflater li = LayoutInflater.from(this);
                View view = li.inflate(R.layout.help, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Help page");
                builder.setView(view).create().show();

//				new AlertDialog.Builder(this)
//					.setTitle("Help page")
//					.setMessage(R.string.help)
//                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog, int which) {
//
//						}
//					})
//					.setIcon(android.R.drawable.ic_menu_help)
//					.show();
				
				break;
				
			case R.id.start:
				actions.CreateMainFolder();

                Toast.makeText(this, "*ROOTED ONLY* " +
                        "This function will work only on rooted devices", Toast.LENGTH_LONG).show();
				File folder = new File(Environment.getExternalStorageDirectory() + "/MANUAL/workflow");
				if (!folder.exists()) {
					folder.mkdirs();
				}
				//start service
				startService(globalService);
				break;
				
			case R.id.stop:
				stopService(globalService);
				Toast.makeText(this, "Stop Service", Toast.LENGTH_SHORT).show();
				break;
				
			case R.id.empty:
				new AlertDialog.Builder(this)
					.setTitle("Empty workflow")
					.setMessage("All your files will be deleted!")
					.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) { 
							stopService(globalService);
							//todo
							File flow = new File(Environment.getExternalStorageDirectory() + "/MANUAL/workflow");
							deleteDirPng(flow);
							File sett = new File(Environment.getExternalStorageDirectory() + "/MANUAL/settings");
							deleteDirTxt(sett);
						}
					})
					.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) { 
							// do nothing
						}
					})
					.setIcon(android.R.drawable.ic_menu_delete)
					.show();
				break;
				
		}
		return true;
	}

}

