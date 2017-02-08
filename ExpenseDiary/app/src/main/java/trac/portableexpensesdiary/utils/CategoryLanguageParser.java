package trac.portableexpensesdiary.utils;

import android.content.Context;

import trac.portableexpensesdiary.R;

public class CategoryLanguageParser {

    public static String getParsedCategoryName(Context context, String categoryName) {

        switch (categoryName) {

            case Constants.DEFAULT_CATEGORY_FOOD:

                return context.getString(R.string.manual_reg_food_item);

            case Constants.DEFAULT_CATEGORY_ALCOHOL:

                return context.getString(R.string.manual_reg_alcohol_item);

            case Constants.DEFAULT_CATEGORY_INCOME:

                return context.getString(R.string.income_tag);

            case Constants.DEFAULT_CATEGORY_PUBLIC_TRANSPORT:

                return context.getString(R.string.manual_reg_transport_item);

            case Constants.DEFAULT_CATEGORY_FUEL:

                return context.getString(R.string.manual_reg_fuel_item);

            case Constants.DEFAULT_CATEGORY_CREDIT:

                return context.getString(R.string.manual_reg_credit_item);

            case Constants.DEFAULT_CATEGORY_BILLS:

                return context.getString(R.string.manual_reg_bills_item);

            case Constants.DEFAULT_CATEGORY_MEDICAL_DRUGS:

                return context.getString(R.string.manual_reg_meds_item);

            case Constants.DEFAULT_CATEGORY_SMOKES:

                return context.getString(R.string.manual_reg_smokes_item);
        }

        return categoryName;
    }

    public static String getTranslatedCategoryName(Context context, String translatedCategoryName) {

        if(translatedCategoryName.equals(context.getString(R.string.manual_reg_food_item))) {

            return Constants.DEFAULT_CATEGORY_FOOD;
        } else if(translatedCategoryName.equals(context.getString(R.string.manual_reg_alcohol_item))) {

            return Constants.DEFAULT_CATEGORY_ALCOHOL;
        } else if(translatedCategoryName.equals(context.getString(R.string.income_tag))) {

            return Constants.DEFAULT_CATEGORY_INCOME;
        } else if(translatedCategoryName.equals(context.getString(R.string.manual_reg_transport_item))) {

            return Constants.DEFAULT_CATEGORY_PUBLIC_TRANSPORT;
        } else if(translatedCategoryName.equals(context.getString(R.string.manual_reg_fuel_item))) {

            return Constants.DEFAULT_CATEGORY_FUEL;
        } else if(translatedCategoryName.equals(context.getString(R.string.manual_reg_credit_item))) {

            return Constants.DEFAULT_CATEGORY_CREDIT;
        } else if(translatedCategoryName.equals(context.getString(R.string.manual_reg_bills_item))) {

            return Constants.DEFAULT_CATEGORY_BILLS;
        } else if(translatedCategoryName.equals(context.getString(R.string.manual_reg_meds_item))) {

            return Constants.DEFAULT_CATEGORY_MEDICAL_DRUGS;
        } else if (translatedCategoryName.equals(context.getString(R.string.manual_reg_smokes_item))) {

            return Constants.DEFAULT_CATEGORY_SMOKES;
        }

        return translatedCategoryName;
    }
}
