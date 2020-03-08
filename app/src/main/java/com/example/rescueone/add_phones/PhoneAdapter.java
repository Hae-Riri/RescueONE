package com.example.rescueone.add_phones;

import android.content.Context;
import android.os.health.PackageHealthStats;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
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

    private LayoutInflater mLayoutInflater;
    private List<ReceiveData> receivers;
    public SparseBooleanArray selectedItems;

    //생성자를 통해 데이터 리스트 객체를 전달받기
    public PhoneAdapter(Context context, ArrayList<EmergencyContact>datas){
        this.context = context;
        this.datas = datas;

        selectedItems = new SparseBooleanArray(0);
    }

    //데이터 변경
    public void setList(List<ReceiveData> data){
        receivers = data;
        notifyDataSetChanged();
    }

    //아이템 선택상태 반환
    public SparseBooleanArray getItemState(){
        return selectedItems;
    }

    public void clearState(){
        selectedItems.clear();
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
    }
    @Override
    public int getItemCount() {
        return datas.size();
    }

    //itemView를 저장하는 ViewHolder 클래스
    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemPhonesBinding mBinding;

        private TextView receiverName;
        private TextView receiverPhone;
        private CheckBox checkBox;
        public View view;


        public ViewHolder(ItemPhonesBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
        }
        public void bind(EmergencyContact emergencyContact){
            mBinding.name.setText(emergencyContact.getName());
            mBinding.number.setText(emergencyContact.getNumber());
            mBinding.checkBox.setChecked(false);
        }
        public void OnClick(View view){
            if(view.equals(mBinding)){
                int position = getAdapterPosition();
                mBinding.checkBox.setChecked(!checkBox.isChecked());
                if(selectedItems.get(position,false)){
                    selectedItems.put(position,false);
                    mBinding.checkBox.setChecked(false);
                }
                else {
                    selectedItems.put(position,true);
                    mBinding.checkBox.setChecked(true);
                }
            }

        }
    }


}
