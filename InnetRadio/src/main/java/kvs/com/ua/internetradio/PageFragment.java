package kvs.com.ua.internetradio;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PageFragment extends Fragment {

    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
    private String[]  radioStations;
    private int pageNumber;
    private ListView listForRadioStation;

    static PageFragment newInstance(int page) {
        PageFragment pageFragment = new PageFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, null);
        listForRadioStation = (ListView) rootView.findViewById(R.id.listViewForRadioStation);
        ArrayAdapter<String> stringArrayAdapter;
        getListContent(pageNumber);
        stringArrayAdapter = new ArrayAdapter<>(getActivity(),R.layout.list_item, radioStations);
        listForRadioStation.setAdapter(stringArrayAdapter);
        listForRadioStation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                 if(pageNumber<2) {
                     Intent intent = new Intent(getActivity(), PlayerActivity.class);
                     intent.putExtra("POSITIONINLIST", position);
                     intent.putExtra("PAGENUMBER", pageNumber);
                     startActivity(intent);
                 }

            }
        });
        return rootView;
    }

    public String[] getListContent(int page){

            switch (page) {
                case 0:
                    return radioStations = getResources().getStringArray(R.array.rock);
                case 1:
                    return radioStations = getResources().getStringArray(R.array.metal);

            }
        return radioStations = getResources().getStringArray(R.array.empty);
    }


}