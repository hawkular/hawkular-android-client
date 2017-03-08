package org.hawkular.client.android.fragment;

import org.hawkular.client.android.R;
import org.hawkular.client.android.backend.model.Trigger;
import org.hawkular.client.android.util.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import icepick.State;


public class TriggerDetailFragment extends android.support.v4.app.Fragment {
            @State
            @Nullable
            Trigger trigger;

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_container,container,false);
    }

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        trigger = getArguments().getParcelable(Fragments.Arguments.TRIGGER);
        Toast.makeText(getActivity(), trigger.getId()+" "+trigger.getDescription(), Toast.LENGTH_SHORT).show();
    }
}
