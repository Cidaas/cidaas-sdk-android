package com.example.widasrnarayanan.cidaas_sdk_androidv2;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cidaasv2.Controller.Cidaas;
import com.example.cidaasv2.Helper.Enums.Result;
import com.example.cidaasv2.Helper.Extension.WebAuthError;

import java.util.Dictionary;
import java.util.Hashtable;

import widas.cidaassdkv2.cidaasnativev2.View.CidaasNative;
import widas.cidaassdkv2.cidaasnativev2.data.Entity.AuthRequest.AuthRequestResponseEntity;
import widas.cidaassdkv2.cidaasnativev2.data.Entity.Deduplication.DeduplicationResponseEntity;
import widas.cidaassdkv2.cidaasnativev2.data.Entity.Register.RegisterUser.RegisterNewUserResponseEntity;
import widas.cidaassdkv2.cidaasnativev2.data.Entity.Register.RegistrationCustomFieldEntity;
import widas.cidaassdkv2.cidaasnativev2.data.Entity.Register.RegistrationEntity;
import widas.cidaassdkv2.cidaasnativev2.data.Entity.Register.RegistrationSetup.RegistrationSetupResponseEntity;
import widas.cidaassdkv2.cidaasnativev2.data.Entity.Register.RegistrationSetup.RegistrationSetupResultDataEntity;

public class RegisterActivity extends AppCompatActivity {

    //Declare Global Variables
    Cidaas cidaas;
    CidaasNative cidaasNative;
    String requestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Intialise variables
        cidaas=new Cidaas(this);
        cidaasNative=new CidaasNative(this);


    }


    public void getRegisterFileds(View view)
    {
        //Call requestId and get registration Details
        cidaasNative.getRequestId(new Result<AuthRequestResponseEntity>() {
            @Override
            public void success(AuthRequestResponseEntity result) {
                //todo On Success Of Request id Call registration service
                requestId=result.getData().getRequestId();
                cidaasNative.getRegistrationFields(result.getData().getRequestId(), "en_US", new Result<RegistrationSetupResponseEntity>() {
                    @Override
                    public void success(RegistrationSetupResponseEntity result) {

                        RegistrationSetupResultDataEntity[] registrationSetupResultDataEntity=result.getData();

                        ScrollView sv = new ScrollView(getApplicationContext());
                        sv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                        RelativeLayout relativeLayout=findViewById(R.id.relativeRegister);
                        //


                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        //Create a layout---------------
                        LinearLayout linearLayout = new LinearLayout(getApplicationContext());
                        linearLayout.setOrientation(LinearLayout.VERTICAL);
                        linearLayout.setVerticalScrollBarEnabled(true);
                        linearLayout.setHorizontalScrollBarEnabled(true);
                        linearLayout.setVerticalScrollbarPosition(1);
                        relativeLayout.addView(linearLayout);
                        // sv.addView(relativeLayout);
                        for (RegistrationSetupResultDataEntity registrationSetupResultData:registrationSetupResultDataEntity
                        ) {


                            //----Create a TextView------
                       /* EditText editText = new EditText(getApplicationContext());
                             //editText.setText("This TextView is dynamically created");
                            editText.setHint(registrationSetupResultData.getFieldKey());
                            editText.setLayoutParams(params);
                        linearLayout.addView(editText);*/
                        }

                        //-----Create a Button--------
                        Button button = new Button(getApplicationContext());
                        button.setText("This Button is dynamically created");
                        LinearLayout.LayoutParams paramsforButton=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        paramsforButton.setMargins(8,8,8,8);

                        button.setLayoutParams(paramsforButton);

                        //---Add all elements to the layout


                        linearLayout.addView(button);

                        //---Create a layout param for the layout-----------------
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
                        // addContentView(linearLayout, layoutParams);
                        Toast.makeText(RegisterActivity.this,"Get Registration Setup"+ result.getStatus(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void failure(WebAuthError error) {
                        Toast.makeText(RegisterActivity.this,"Get Registration Setup Fails "+ error.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void failure(WebAuthError error) {
                // on Error Display the error message
                Toast.makeText(RegisterActivity.this, "RequestId Fails"+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void Register(View view){

        RegistrationEntity registrationEntity=new RegistrationEntity();
        registrationEntity.setUsername("Raj");
        registrationEntity.setEmail("cidaaswidaa@gmail.com");
        registrationEntity.setGiven_name("RajaS");
        registrationEntity.setFamily_name("RsdfsN");
        registrationEntity.setPassword("123456");
        registrationEntity.setPassword_echo("123456");
        registrationEntity.setMobile_number("+919876553231");
       // Date date=new Date();
      //  date.setDate(27/12/1994);
      //  registrationEntity.setBirthdate(date);
        registrationEntity.setGender("Male");
        registrationEntity.setWebsite("http://google.com");


        RegistrationCustomFieldEntity registrationCustomFieldEntity=new RegistrationCustomFieldEntity();
        registrationCustomFieldEntity.setKey("Pincode");
        registrationCustomFieldEntity.setValue("123456");
        registrationCustomFieldEntity.setDataType("String");
        registrationCustomFieldEntity.setId("pincode");
        registrationCustomFieldEntity.setInternal(true);

        RegistrationCustomFieldEntity registrationCustomFieldEntity1=new RegistrationCustomFieldEntity();
        registrationCustomFieldEntity1.setKey("Age");
        registrationCustomFieldEntity1.setValue("24");
        registrationCustomFieldEntity1.setDataType("String");
        registrationCustomFieldEntity1.setId("age");

        RegistrationCustomFieldEntity registrationCustomFieldEntity2=new RegistrationCustomFieldEntity();
        registrationCustomFieldEntity2.setKey("Address");
        registrationCustomFieldEntity2.setValue("28,Kuttiyananjan Street Sivakasi");
        registrationCustomFieldEntity2.setDataType("String");
        registrationCustomFieldEntity2.setId("address");

        Dictionary<String, RegistrationCustomFieldEntity> customFileds = new Hashtable<>();

        customFileds.put(registrationCustomFieldEntity.getKey(),registrationCustomFieldEntity);
        customFileds.put(registrationCustomFieldEntity1.getKey(),registrationCustomFieldEntity1);
        customFileds.put(registrationCustomFieldEntity2.getKey(),registrationCustomFieldEntity2);

        registrationEntity.setCustomFields(customFileds);

        cidaasNative.registerUser(requestId, registrationEntity, new Result<RegisterNewUserResponseEntity>() {
            @Override
            public void success(RegisterNewUserResponseEntity result) {
                Toast.makeText(RegisterActivity.this, "Register Successfully"+result.getData().getSuggested_action()+result.getData().getNext_token(), Toast.LENGTH_SHORT).show();

                if(result.getData().getSuggested_action().equalsIgnoreCase("DEDUPLICATION"))
                {
                   //cidaas.loginWithCredentials();
                    cidaasNative.getDeduplicationDetails(result.getData().getTrackId(), new Result<DeduplicationResponseEntity>() {
                        @Override
                        public void success(DeduplicationResponseEntity result) {
                            Toast.makeText(RegisterActivity.this, ""+result.getData().getEmail(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void failure(WebAuthError error) {
                            Toast.makeText(RegisterActivity.this, ""+error.getErrorMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }

            @Override
            public void failure(WebAuthError error) {
                Toast.makeText(RegisterActivity.this, "Register Failed"+error.getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
//Fetch Details


}
