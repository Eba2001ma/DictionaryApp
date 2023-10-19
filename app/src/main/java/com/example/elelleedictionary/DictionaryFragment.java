package com.example.elelleedictionary;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class DictionaryFragment extends Fragment {

    private FragmentListener listener;
    private String value = "Welcome Ebba";
    ArrayAdapter<String> adapter;
    private ArrayList<String> mSource = new ArrayList<String>();
    ListView dicList;
    public DictionaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dictionary, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
     /*
        Button myButton = (Button) view.findViewById(R.id.myBtn);
        myButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (listener!=null)
                    listener.onItemClick(value);
            }

        });
        */
        dicList = view.findViewById(R.id.myDic);
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,mSource);
        dicList.setAdapter(adapter);
        dicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (listener!=null)
                    listener.onItemClick(mSource.get(position));
            }
        });


    }
    public void resetDataSource(ArrayList<String> source){
        mSource = source;
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,mSource);
        dicList.setAdapter(adapter);
    }
    public void filterValue(String value){
        int size = adapter.getCount();
        for(int i=0; i<size; i++){
            if (adapter.getItem(i).startsWith(value)){
                dicList.setSelection(i);
                break;
            }
        }
    }
   /*
   String[] getListOfWord(){
        String[] source = new String[]{
                "a"
                ,"aardvark"
                ,"aback"
                ,"abacus"
                ,"abandon"
                ,"abandoned"
                ,"abase"
                ,"abasement"
                ,"abashed"
                ,"abate"
                ,"abattoir"
                ,"abbess"
                ,"abbey"
                ,"abbot"
                ,"abbreviation"
                ,"abdicate"
        };
        return source;
    }
    */

    public void setOnFragmentListener(FragmentListener listener){
        this.listener=listener;

    }

}