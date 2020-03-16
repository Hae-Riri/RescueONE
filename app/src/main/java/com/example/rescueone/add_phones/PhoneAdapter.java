package com.example.rescueone.add_phones;

import android.content.Context;
import android.os.health.PackageHealthStats;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rescueone.databinding.ItemPhonesBinding;
import com.example.rescueone.db_phone.ReceiveData;
import com.example.rescueone.db_server.EmergencyContact;

import java.util.ArrayList;
import java.util.List;

public class PhoneAdapter extends RecyclerView.Adapter<PhoneAdapter.ViewHolder> {

    Context context;
    ArrayList<EmergencyContact> datas;

    //itemView를 저장하는 ViewHolder 클래스, 리스너를 구현할 예정인 함수
    public class ViewHolder extends RecyclerView.ViewHolder
    implements View.OnCreateContextMenuListener{
        ItemPhonesBinding mBinding;
        public View view;

        public ViewHolder(ItemPhonesBinding mBinding) {
            super(mBinding.getRoot());//mBinding의 getRoot 값이 view임
            this.mBinding = mBinding;
            mBinding.getRoot().setOnCreateContextMenuListener(this);
        }
        public void bind(EmergencyContact emergencyContact){
            mBinding.name.setText(emergencyContact.getName());
            mBinding.number.setText(emergencyContact.getNumber());
        }
        //컨텍스트 메뉴를 생성하고 메뉴 항목 선택 시 호출되는 리스너를 등록해 줌
        //id 1001, 1002로 편집,삭제 중 뭘 선택했는지 리스너에서 구분
        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            //MenuItem Edit = menu.add(Menu.NONE,1001,1,"편집");
            MenuItem Delete = menu.add(Menu.NONE,1002,2,"삭제");
            //Edit.setOnMenuItemClickListener(onEditMenu);
            Delete.setOnMenuItemClickListener(onEditMenu);
        }
        //4.컨텍스트 메뉴에서 항목 클릭 시의 동작(삭제,편집)
        private final MenuItem.OnMenuItemClickListener onEditMenu =
                new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()){
                            case 1002://삭제

                                //firebase에서의 삭제
                                ((AddPhonesActivity)context).deleteServerDB(getAdapterPosition());

                                //View에서의 삭제
//                                datas.remove(7 - (getAdapterPosition()+1));
//                                notifyItemRemoved(getAdapterPosition());
//                                notifyItemRangeChanged(getAdapterPosition(),datas.size());
                                break;
                        }
                        return true;
                    }
                };
    }


    //생성자를 통해 데이터 리스트 객체를 전달받기
    public PhoneAdapter(Context context, ArrayList<EmergencyContact>datas){
        this.context = context;
        this.datas = datas;
    }
    //viewType 형태의 아이템 뷰를 위한 ViewHolder 객체를 생성하여 리턴
    //위에서 작성한 ViewHolder의 객체를 생성
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPhonesBinding mBinding = ItemPhonesBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent,false);
        //생성한 ViewHoler객체 리턴
        return new ViewHolder(mBinding);
    }

    //위에서 작성한 ViewHolder의 객체를 화면에 표시
    //position에 해당하는 데이터를 ViewHolder의 itemView에 표시하는 함수
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(datas.get(position));
        //bind함수 안에 각각의 id에 어떤 값을 세팅할지 구현을 해 둠
        //해당값을 표시하는 함수인데 bind함수를 미리 정의해서 그 안에 구현을 해 둔 것.
    }
    @Override
    public int getItemCount() {
        return datas.size();
    }




}
