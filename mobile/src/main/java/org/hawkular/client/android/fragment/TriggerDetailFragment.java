/*
 * Copyright 2015-2017 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hawkular.client.android.fragment;

import org.hawkular.client.android.animation.MyBounceInterpolator;
import org.hawkular.client.android.R;
import org.hawkular.client.android.backend.model.Trigger;
import org.hawkular.client.android.util.Fragments;
import org.jboss.aerogear.android.store.DataManager;
import org.jboss.aerogear.android.store.generator.IdGenerator;
import org.jboss.aerogear.android.store.sql.SQLStore;
import org.jboss.aerogear.android.store.sql.SQLStoreConfiguration;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.State;


public class TriggerDetailFragment extends android.support.v4.app.Fragment {
            @State
            @Nullable
            Trigger trigger;

    @BindView(R.id.favourite_button)
    ImageButton addButton;


    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trigger_detail,container,false);
    }

    @OnClick(R.id.favourite_button)
    public void toggleTriggerFavouriteStatus(){
        Context context = getActivity();
        final Animation myAnim = AnimationUtils.loadAnimation(context, R.anim.bounce);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);


        addButton.startAnimation(myAnim);

        SQLStore<Trigger> store = openStore(context);

        if (store.read(trigger.getId()) == null) {
            store.save(trigger);
            addButton.setColorFilter(getResources().getColor(R.color.background_primary));
            addButton.startAnimation(myAnim);
        } else {
            store.remove(trigger.getId());
            addButton.setColorFilter(getResources().getColor(R.color.background_secondary));
            addButton.startAnimation(myAnim);
        }

        store.close();
    }

    private SQLStore<Trigger> openStore(Context context) {
        DataManager.config("FavouriteTriggers", SQLStoreConfiguration.class)
                .withContext(context)
                .withIdGenerator(new IdGenerator() {
                    @Override
                    public String generate() {
                        return UUID.randomUUID().toString();
                    }
                }).store(Trigger.class);
        return (SQLStore<Trigger>) DataManager.getStore("FavouriteTriggers");
    }


    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ButterKnife.bind(this, getView());

        trigger = getArguments().getParcelable(Fragments.Arguments.TRIGGER);
        SQLStore<Trigger> store = openStore(getContext());
               store.openSync();

        if(store.read(trigger.getId()) == null) {
            addButton.setColorFilter(getResources().getColor(R.color.background_secondary));
        } else {
            addButton.setColorFilter(getResources().getColor(R.color.background_primary));
        }

        store.close();
    }
}
