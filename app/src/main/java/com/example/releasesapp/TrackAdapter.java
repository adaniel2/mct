package com.example.releasesapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.RecyclerView;

import com.example.releasesapp.databinding.ItemTrackBinding;

import java.util.ArrayList;
import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackViewHolder> {
    private Context ct;
    private ArrayList<Track> arr;
    private LayoutInflater inflater;

    public TrackAdapter(@NonNull Context context, @NonNull List<Track> objects) {
        this.ct = context;
        this.arr = new ArrayList<>(objects);
        inflater = LayoutInflater.from(ct);
    }

    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTrackBinding binding = ItemTrackBinding.inflate(inflater, parent, false);
        return new TrackViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackViewHolder holder, int position) {
        Track t = arr.get(position);
        holder.bind(t);
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }

    public class TrackViewHolder extends RecyclerView.ViewHolder {
        private ItemTrackBinding binding;

        public TrackViewHolder(@NonNull ItemTrackBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Track track) {
            binding.progressCircleText.setText(Float.toString(track.getProgress()) + '%');
            binding.trackProgressCircle.setProgress((int)track.getProgress());

            binding.cardTrackName.setText(track.getTrackName());
            binding.cardIsrc.setText("ISRC: " + track.getIsrc());

            View TrackCardBinding = binding.trackCard;
            View TrackCardButton = binding.trackCardButton;
            View TrackCardParentBinding = binding.trackCardParent;

            View.OnClickListener cardClickedListener = new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final SpringAnimation scaleXAnimation = new SpringAnimation(TrackCardParentBinding, DynamicAnimation.SCALE_X, 1);
                    final SpringAnimation scaleYAnimation = new SpringAnimation(TrackCardParentBinding, DynamicAnimation.SCALE_Y, 1);

                    //set spring stiffness and damping ratio
                    scaleXAnimation.getSpring().setStiffness(SpringForce.STIFFNESS_HIGH);
                    scaleXAnimation.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);
                    scaleYAnimation.getSpring().setStiffness(SpringForce.STIFFNESS_HIGH);
                    scaleYAnimation.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);

                    //set the final scale value, for example to 1.1f for zoom in or 0.9f for zoom out
                    float finalScaleValue = 1.05f;
                    scaleXAnimation.animateToFinalPosition(finalScaleValue);
                    scaleYAnimation.animateToFinalPosition(finalScaleValue);

                    scaleXAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                        @Override
                        public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                            // Zoom back to the original form
                            final SpringAnimation scaleXAnimationBack = new SpringAnimation(TrackCardParentBinding, DynamicAnimation.SCALE_X, 1);
                            final SpringAnimation scaleYAnimationBack = new SpringAnimation(TrackCardParentBinding, DynamicAnimation.SCALE_Y, 1);

                            scaleXAnimationBack.getSpring().setStiffness(SpringForce.STIFFNESS_MEDIUM);
                            scaleXAnimationBack.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_HIGH_BOUNCY);
                            scaleYAnimationBack.getSpring().setStiffness(SpringForce.STIFFNESS_MEDIUM);
                            scaleYAnimationBack.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_HIGH_BOUNCY);

                            scaleXAnimationBack.start();
                            scaleYAnimationBack.start();
                        }
                    });

                    scaleXAnimation.start();
                    scaleYAnimation.start();
                }

            };

            TrackCardBinding.setOnClickListener(cardClickedListener);
            TrackCardButton.setOnClickListener(cardClickedListener);
        }

    }

}