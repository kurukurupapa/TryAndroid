package com.example.kurukurupapa.clipboard01;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListenerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListenerFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * クリップボードの監視を行う練習です。
 *
 * 参考
 * PrimaryClipChangedListenerを使用してクリップボードの値変更を検知する | Tech Booster
 * http://techbooster.org/android/application/3988/
 *
 * ClipboardManager | Android Developers
 * http://developer.android.com/reference/android/content/ClipboardManager.html
 */
public class ListenerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /** クリップボード変更リスナー */
    private ClipboardManager.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListenerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListenerFragment newInstance(String param1, String param2) {
        ListenerFragment fragment = new ListenerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ListenerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_listener, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onListenerFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        // リスナーの作成
        if (mOnPrimaryClipChangedListener == null) {
            mOnPrimaryClipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() {
                @Override
                public void onPrimaryClipChanged() {
                    Toast.makeText(getActivity(), "クリップボードの変更を検知！", Toast.LENGTH_LONG).show();

                    ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Activity.CLIPBOARD_SERVICE);
                    ClipData clipData = cm.getPrimaryClip();
                    String text = clipData.toString();

                    TextView clipTextView = (TextView) getActivity().findViewById(R.id.clip_textView);
                    clipTextView.setText(text);
                }
            };
        }

        // クリップボード監視の開始
        ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Activity.CLIPBOARD_SERVICE);
        cm.addPrimaryClipChangedListener(mOnPrimaryClipChangedListener);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

        // クリップボード監視の終了
        ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Activity.CLIPBOARD_SERVICE);
        cm.removePrimaryClipChangedListener(mOnPrimaryClipChangedListener);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onListenerFragmentInteraction(Uri uri);
    }

}
