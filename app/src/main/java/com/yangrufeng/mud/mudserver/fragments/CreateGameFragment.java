package com.yangrufeng.mud.mudserver.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.yangrufeng.mud.mudserver.MainActivity;
import com.yangrufeng.mud.mudserver.R;
import com.yangrufeng.mud.mudserver.common.Common;


public class CreateGameFragment extends Fragment {

    private Button createGameButton;
    private RadioGroup modGroup;
    private EditText playerNumText;

    // TODO: Rename parameter arguments, choose names that match
/*    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";*/

    // TODO: Rename and change types of parameters
/*    private String mParam1;
    private String mParam2;*/

/*    private OnFragmentInteractionListener mListener;*/

    public CreateGameFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
/*    public static CreateGameFragment newInstance(String param1, String param2) {
        CreateGameFragment fragment = new CreateGameFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_create_game, container, false);
        createGameButton = (Button)v.findViewById(R.id.create_game_btn);
        playerNumText = (EditText)v.findViewById(R.id.playernum_value);
        modGroup=(RadioGroup)v.findViewById(R.id.mod_group);
        modGroup.setOnCheckedChangeListener(listen);
        createGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity main = (MainActivity)getActivity();
                if(playerNumText.getText() == null || "".equals(playerNumText.getText().toString())) {
                    showErrorDialog("请输入玩家人数！");
                    return;
                }
                int playerNum = Integer.parseInt(playerNumText.getText().toString());
                if(playerNum < 1 || playerNum > 8) {
                    showErrorDialog("玩家人数不得小于1人且不能多于8人");
                    return;
                }
                if(main.getMod()!=null&&main.getMod().equals(Common.MOD_SURVIVAL)&&playerNum<2) {
                    showErrorDialog("大逃杀MOD玩家人数不得小于2人");
                    return;
                }
                main.setPlayerNum(playerNum);

                main.replaceFragment("MAIN");
            }
        });
        return v;
    }

    private RadioGroup.OnCheckedChangeListener listen=new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int id= group.getCheckedRadioButtonId();
            MainActivity main = (MainActivity)getActivity();
            switch (id) {
                case R.id.survival_radio:
                    main.setMod(Common.MOD_SURVIVAL);
                    break;
                default:
                    main.setMod(Common.MOD_NONE);
                    break;
                }
            }
    };
    // 简单消息提示框
    private void showErrorDialog(String msg){
        new AlertDialog.Builder(getActivity())
                .setTitle("错误提示")
                .setMessage(msg)
                .setPositiveButton("确定", null)
                .show();
    }
/*    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
/*        mListener = null;*/
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
/*    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/
}
