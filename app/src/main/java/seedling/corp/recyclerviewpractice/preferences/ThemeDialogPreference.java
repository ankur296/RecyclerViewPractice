package seedling.corp.recyclerviewpractice.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import seedling.corp.recyclerviewpractice.R;
import seedling.corp.recyclerviewpractice.activity.MainActivity;
import seedling.corp.recyclerviewpractice.activity.SettingsActivity;
import seedling.corp.recyclerviewpractice.utils.Utils;

/**
 * Created by Ankur Nigam on 19-01-2016.
 */
public class ThemeDialogPreference extends DialogPreference{

    Context mContext;
    public ThemeDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        setDialogLayoutResource(R.layout.dialog_color_picker);
        setDialogTitle(R.string.popup_color_title);
        setDialogIcon(null);
    }

    @Override
    protected void onBindDialogView(View view) {

        ImageView redImageView,greenImageView;
        redImageView = (ImageView)view.findViewById(R.id.iv_red);
        greenImageView = (ImageView)view.findViewById(R.id.iv_green);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getContext()
                                .getSharedPreferences("theme",((SettingsActivity)mContext).MODE_PRIVATE)
                                .edit();

                switch (v.getId()) {

                    case R.id.iv_green:
                        Utils.changeToTheme( (SettingsActivity)mContext, Utils.THEME_GREEN);
                        editor.putString("color","green").apply();
                        break;

                    case R.id.iv_red:
                        Utils.changeToTheme( (SettingsActivity)mContext, Utils.THEME_RED);
                        editor.putString("color","red").apply();
                        break;
                }

            }
        };

        redImageView.setOnClickListener(listener);
        greenImageView.setOnClickListener(listener);

        super.onBindDialogView(view);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        // When the user selects "OK", persist the new value
        if (positiveResult) {
//            persistString()Int(mNewValue);
        }
    }
}
