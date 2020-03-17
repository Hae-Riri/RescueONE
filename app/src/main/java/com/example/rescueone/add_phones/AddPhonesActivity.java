package com.example.rescueone.add_phones;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.rescueone.R;
import com.example.rescueone.activity.LoginActivity;
import com.example.rescueone.activity.MainActivity;
import com.example.rescueone.databinding.ActivityAddPhonesBinding;
import com.example.rescueone.db_phone.DBService;
import com.example.rescueone.db_phone.ReceiveData;
import com.example.rescueone.db_phone.ReceiveDataRepository;
import com.example.rescueone.db_server.EmergencyContact;
import com.example.rescueone.db_server.User;
import com.example.rescueone.permission.PermissionManager;
import com.example.rescueone.sos.MessageManager;
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

    //내부db
    private ReceiveDataRepository mRepository;
    //DBService dbService = new DBService(this);

    private MessageManager sms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRepository = new ReceiveDataRepository(getApplication());

        //권한 설정 허용 확인
        if(PermissionManager.checkPermission(this, Manifest.permission.SEND_SMS)){
            PermissionManager.getPermission(this, Manifest.permission.SEND_SMS);
        }
        sms = new MessageManager(AddPhonesActivity.this);


        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_add_phones);
        mBinding.setActivity(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUser = mAuth.getCurrentUser();

        mBinding.rv.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(),
                mBinding.rv,new ClickListener(){
            @Override
            public void onClick(View view, int position) {
                EmergencyContact contact = datas.get(position);
                Toast.makeText(getApplicationContext(),contact.getName()+' '+contact.getNumber()
                +' ',Toast.LENGTH_LONG).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    public interface ClickListener{
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }
    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{
        private GestureDetector gestureDetector;
        private AddPhonesActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView,
                                     final AddPhonesActivity.ClickListener clickListener){
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(),e.getY());
                    if(child!=null && clickListener!=null){
                        clickListener.onLongClick(child,recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(),e.getY());
            if(child!=null && clickListener!=null && gestureDetector.onTouchEvent(e)){
                clickListener.onClick(child,rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }

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
        //현재의 리사이클러뷰에 누구의 데이터를 가져올 건지 확인해서 datas로 가져옴.
        if(user.getEmergencyContact()!=null && user.getEmergencyContact().size()!=0){
            //하나씩 묶어서 다 읽어오기 for문으로
            for(Map.Entry<String,String> entry : user.getEmergencyContact().entrySet()){
                //읽어서 ArrayList datas에 하나씩 저장 - 이름, 번호 순으로 저장
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
        //adapter.notifyDataSetChanged();
        mBinding.rv.setAdapter(adapter);

    }

    public void OnClick(View view){
        if(view.equals(mBinding.add)){
            if(adapter.getItemCount() < 8){
                AddPhonesDialog addPhoneDialog = new AddPhonesDialog(this,uid);
                addPhoneDialog.show();
            }else{
                Toast.makeText(this,"연락처는 8개까지 저장 가능합니다.",Toast.LENGTH_LONG).show();
            }
        }

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

        else if(view.equals(mBinding.back)){
            Intent intent2 = new Intent(this, MainActivity.class);
            startActivity(intent2);
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        }
    }
    public void addData(EmergencyContact data) {
        datas.add(data);
        setUser(currentUser);
        //adapter.notifyDataSetChanged();

        //내부db
        String receiverName = data.getName();
        String receiverPhone = data.getNumber();
        mRepository.insertData(new ReceiveData(receiverName,receiverPhone));
        //dbService.insert(new ReceiveData(receiverName,receiverPhone));

        //설치문자
        sms.sendLink(receiverPhone);
        Toast.makeText(AddPhonesActivity.this,"상대방에게 설치알림 문자가 발송되었습니다.",Toast.LENGTH_LONG).show();
    }
    public void deleteServerDB(int position){
        String receiverName = datas.get(position).getName();
        String receiverPhone = datas.get(position).getNumber();

        String deleteNum = datas.get(position).getNumber();
        mDatabase.child("users").child(uid).child("emergencyContact")
                .child(deleteNum).removeValue();
        //adapter.notifyDataSetChanged();
        setUser(currentUser);

        //내부db
        //dbService.deleteByPhone(receiverPhone);
        mRepository.deleteData(new ReceiveData(receiverName,receiverPhone));
    }

}
