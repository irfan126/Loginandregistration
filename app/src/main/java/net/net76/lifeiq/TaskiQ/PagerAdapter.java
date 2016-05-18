package net.net76.lifeiq.TaskiQ;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Administrator on 15/08/2015.
 */
public class PagerAdapter  extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                DateFragment tab1 = new DateFragment();
                return tab1;
            case 1:
                CategoryFragment tab2 = new CategoryFragment();
                return tab2;

         //   case 2:
           //     DetailFragment tab3 = new DetailFragment();
             //   return tab3;

          default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}