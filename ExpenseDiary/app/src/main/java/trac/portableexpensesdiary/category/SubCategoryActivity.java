package trac.portableexpensesdiary.category;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.raizlabs.android.dbflow.data.Blob;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;
import java.util.UUID;

import trac.portableexpensesdiary.R;
import trac.portableexpensesdiary.basecomponents.AntiMonkeyButton;
import trac.portableexpensesdiary.basecomponents.BaseActivity;
import trac.portableexpensesdiary.model.Category;
import trac.portableexpensesdiary.model.SubCategory;
import trac.portableexpensesdiary.utils.Constants;
import trac.portableexpensesdiary.utils.ImageUtils;

public class SubCategoryActivity extends BaseActivity implements View.OnClickListener {

    private ImageView subCategoryImageView;
    private TextView subCategoryTitleTxt;
    private TextInputLayout subCategoryNameInputLayout;
    private EditText subCategoryNameTxt;
    private EditText subCategoryDescription;
    private Uri subCategoryImageUri;

    private Category parentCategory;
    private SubCategory currentSubCategory;

    private boolean isImageSet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sub_category);
        setTitle("Expense Sub Category");
        initViews();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.CAMERA_CAPTURE) {
            if (resultCode == RESULT_OK) {
                ImageUtils.performCrop(
                        this,
                        subCategoryImageUri,
                        false
                );
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (data == null || resultCode != RESULT_OK) {

                return;
            }

            CropImage.ActivityResult result =
                    CropImage.getActivityResult(data);

            try {
                Uri resultUri = result.getUri();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                        this.getContentResolver(),
                        resultUri
                );

                if (bitmap.getHeight() == bitmap.getWidth()) {
                    subCategoryImageView.setImageBitmap(
                            ThumbnailUtils.extractThumbnail(
                                    bitmap,
                                    Constants.COMPRESSED_IMAGE_WIDTH,
                                    Constants.COMPRESSED_IMAGE_HEIGHT
                            )
                    );

                    isImageSet = true;
                }
            } catch (IOException e) {
                e.printStackTrace();

                Toast.makeText(
                        this,
                        "Error occurred when trying to get the cropped image!",
                        Toast.LENGTH_SHORT
                ).show();
            }
        } else {
            if (data == null || resultCode != RESULT_OK) {

                return;
            }
            subCategoryImageUri = data.getData();

            ImageUtils.performCrop(
                    this,
                    subCategoryImageUri,
                    false
            );
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.takePhotoBtn:
                subCategoryImageUri = ImageUtils.takePhoto(
                        this,
                        subCategoryImageUri
                );

                break;

            case R.id.selectPhotoBtn:
                ImageUtils.selectPhoto(this);

                break;

            case R.id.removePhotoBtn:
                subCategoryImageView.setImageDrawable(
                        getResources().getDrawable(R.drawable.ic_panorama_fish_eye_white_48dp)
                );

                break;

            case R.id.cancelBtn:
                finish();

                break;

            case R.id.saveBtn:
                saveSubCategory();

                break;
        }
    }

    private void initViews() {
        AntiMonkeyButton takePhotoBtn =
                (AntiMonkeyButton) findViewById(R.id.takePhotoBtn);
        AntiMonkeyButton selectPhotoBtn =
                (AntiMonkeyButton) findViewById(R.id.selectPhotoBtn);
        AntiMonkeyButton removePhotoBtn =
                (AntiMonkeyButton) findViewById(R.id.removePhotoBtn);
        AntiMonkeyButton cancelBtn =
                (AntiMonkeyButton) findViewById(R.id.cancelBtn);
        AntiMonkeyButton saveBtn =
                (AntiMonkeyButton) findViewById(R.id.saveBtn);
        TextView subCategoryParent =
                (TextView) findViewById(R.id.parentCategoryTxt);
        subCategoryTitleTxt =
                (TextView) findViewById(R.id.subCategoryTitle);
        subCategoryImageView =
                (ImageView) findViewById(R.id.categoryImage);
        subCategoryNameInputLayout =
                (TextInputLayout) findViewById(R.id.categoryNameTxtLayout);
        subCategoryNameTxt =
                (EditText) findViewById(R.id.subCategoryName);
        subCategoryDescription =
                (EditText) findViewById(R.id.subCategoryDescription);

        String parentCategoryId =
                getIntent().getStringExtra(Constants.CATEGORY_ID_TAG);
        String subCategoryId =
                getIntent().getStringExtra(Constants.SUB_CATEGORY_ID_TAG);

        if (parentCategoryId != null) {
            parentCategory =
                    Category.getCategoryById(UUID.fromString(parentCategoryId));
            subCategoryParent.setText(parentCategory.getCategoryName());

            if (subCategoryId != null) {
                currentSubCategory =
                        SubCategory.getSubCategory(UUID.fromString(subCategoryId));
                loadData();
            }
        }

        takePhotoBtn.setOnClickListener(this);
        selectPhotoBtn.setOnClickListener(this);
        removePhotoBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
    }

    private void loadData() {
        if (currentSubCategory != null) {
            subCategoryTitleTxt.setText(currentSubCategory.getSubCategoryName());
            subCategoryImageView.setImageDrawable(
                    ImageUtils.blobToDrawable(
                            getResources(),
                            currentSubCategory.getSubCategoryPicture().getBlob()
                    )
            );
            subCategoryNameTxt.setText(
                    currentSubCategory.getSubCategoryName()
            );
            subCategoryDescription.setText(
                    currentSubCategory.getSubCategoryDescription()
            );
        }
    }

    private boolean areFieldsValidated() {
        boolean areValidated = true;
        subCategoryNameInputLayout.setError(null);

        if (subCategoryNameTxt.getText().toString().trim().equals("")) {
            subCategoryNameInputLayout.setError(getString(R.string.empty_field_err));
            subCategoryNameInputLayout.requestFocus();
            areValidated = false;
        }

        SubCategory subCategory = SubCategory.getSubCategoryByName(
                subCategoryNameTxt.getText().toString().trim()
        );

        if(this.currentSubCategory != null) {
            if(subCategory != null && !this.currentSubCategory.getId().equals(subCategory.getId())) {
                subCategoryNameInputLayout.setError(getString(R.string.sub_category_duplication_err));
                subCategoryNameInputLayout.requestFocus();
                areValidated = false;
            }
        } else {
            if(subCategory != null) {
                subCategoryNameInputLayout.setError(getString(R.string.sub_category_duplication_err));
                subCategoryNameInputLayout.requestFocus();
                areValidated = false;
            }
        }

        return areValidated;
    }

    private void saveSubCategory() {
        if (areFieldsValidated()) {
            if (currentSubCategory == null) {
                currentSubCategory = new SubCategory();
            }

            currentSubCategory.setParentCategoryId(
                    parentCategory.getId()
            );
            currentSubCategory.setSubCategoryName(
                    subCategoryNameTxt.getText().toString().trim()
            );
            currentSubCategory.setSubCategoryDescription(
                    subCategoryDescription.getText().toString().trim()
            );

            if (!isImageSet) {
                currentSubCategory.setSubCategoryPicture(
                        new Blob(
                                ImageUtils.defaultDrawableToBlob(
                                        subCategoryImageView.getDrawable()
                                )
                        )
                );
            } else {
                currentSubCategory.setSubCategoryPicture(
                        new Blob(
                                ImageUtils.drawableToBlob(
                                        subCategoryImageView.getDrawable()
                                )
                        )
                );
            }

            currentSubCategory.save();
            parentCategory.setNumberOfSubCategories(
                    parentCategory.getNumberOfSubCategories() + 1
            );
            parentCategory.save();

            Toast.makeText(
                    this,
                    getString(R.string.sub_category_saved_successfully),
                    Toast.LENGTH_SHORT
            ).show();

            finish();
        }
    }
}
