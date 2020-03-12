package com.example.rescueone.add_phones;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;

import com.example.rescueone.R;
import com.example.rescueone.activity.LoginActivity;
import com.example.rescueone.activity.MainActivity;
import com.example.rescueone.databinding.ActivityAddPhonesBinding;
import com.example.rescueone.db_server.EmergencyContact;
import com.example.rescueone.db_server.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class AddPhonesActivity extends AppCompatActivity {

    ActivityAddPhonesBinding mBinding;
    String uid;
    User user;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    PhoneAdapter adapter;
    ArrayList<EmergencyContact> datas = new ArrayList<>();
    FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_add_phones);
        mBinding.setActivity(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUser = mAuth.getCurrentUser();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Check if user is signed in (non-null) and update UI accordingly.

        updateUI(currentUser);
        setUser(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
    }

    private void setUser(FirebaseUser currentUser) {
        if(currentUser ==null){//로그인 실패 시
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        }else{//로그인 성공
            uid = currentUser.getUid();
            mDatabase.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {//비동기처리
                    //이벤트 발생 시점에 데베에서 지정된 위치의 데이터를 포함하는 datasnapshot을 수신하여 실행
                    user = dataSnapshot.getValue(User.class);
                    setRV();//비동기 처리이므로 내부에서 처리해야 함
                    //위에서 하면 너무 빨라서 데이터가 없는 채로 rv띄우게 된다.
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void setRV() {
        datas.clear();
        if(user.getEmergencyContact()!=null && user.getEmergencyContact().size()!=0){
            //하나씩 묶어서 다 읽어오기 for문으로
            for(Map.Entry<String,String> entry : user.getEmergencyContact().entrySet()){
                //읽어서 ArrayList에 하나씩 저장 - 이름, 번호 순으로 저장
                datas.add(new EmergencyContact(entry.getValue(),entry.getKey()));
                //emrgencyContact에서 key가 번호, value가 이름이었는데 recycleView에서 순서는
                //이름 전화번호 순서임
            }
        }
        //LinearLayoutManager로 객체 지정
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mBinding.rv.setHasFixedSize(true);//recyclerView의 크기 고정
        mBinding.rv.setLayoutManager(mLinearLayoutManager);
        //RecyclerView에 올릴 것으로 PhoneAdapter 객체를 지정
        adapter = new PhoneAdapter(this,datas);
        mBinding.rv.setAdapter(adapter);
    }

    public void OnClick(View view){
        if(view.equals(mBinding.add)){
            AddPhonesDialog addPhoneDialog = new AddPhonesDialog(this,uid);
            addPhoneDialog.show();
        }else if(view.equals(mBinding.delete)){

            //TODO:삭제구현하기
            //체크된 삭제예정항목 가져오기
//            SparseBooleanArray state = adapter.getItemState();
//            int i = adapter.getItemCount()-1;
//            for(;i>-1;i--){
//                if(state.get(i)){
//                    //어차피 setRV를 먼저 하는데, 그게 emergencyCantact 데이터를 읽어
//                    //datas를 새로 구성하므로 다시 setRV하면 업데이트된 데이터로 나올듯?
//                    //db에서 해당값 삭제
//                    String deleteNum = datas.get(i).getNumber();
//                    mDatabase.child("users").child(uid).child("emergencyContact")
//                            .child(deleteNum).removeValue();
//                    setUser(currentUser);
//                }
//            }

        }else if(view.equals(mBinding.back)){
            Intent intent2 = new Intent(this, MainActivity.class);
            startActivity(intent2);
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        }
    }
    public void addData(EmergencyContact data) {
        datas.add(data);
    }

}
