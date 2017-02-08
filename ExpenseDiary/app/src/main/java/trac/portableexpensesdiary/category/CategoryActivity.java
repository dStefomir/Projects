package trac.portableexpensesdiary.category;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.raizlabs.android.dbflow.data.Blob;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;

import de.hdodenhof.circleimageview.CircleImageView;
import trac.portableexpensesdiary.R;
import trac.portableexpensesdiary.adapters.SubCategoryAdapter;
import trac.portableexpensesdiary.basecomponents.AntiMonkeyButton;
import trac.portableexpensesdiary.basecomponents.AntiMonkeyGridView;
import trac.portableexpensesdiary.basecomponents.BaseActivity;
import trac.portableexpensesdiary.basecomponents.StaticAntiMonkeyGridView;
import trac.portableexpensesdiary.model.Category;
import trac.portableexpensesdiary.model.ExpenseTrackingDetails;
import trac.portableexpensesdiary.model.SubCategory;
import trac.portableexpensesdiary.statistics.AnalyzeExpenseDetailDialog;
import trac.portableexpensesdiary.utils.AsyncThumbnailImageLoaderTask;
import trac.portableexpensesdiary.utils.Constants;
import trac.portableexpensesdiary.utils.DisplayUtils;
import trac.portableexpensesdiary.utils.ImageUtils;

public class CategoryActivity extends BaseActivity
        implements View.OnClickListener, AdapterView.OnItemClickListener, AbsListView.RecyclerListener {

    private StaticAntiMonkeyGridView subCategoryGridView;
    private SubCategoryAdapter adapter;
    private TextView categoryTitle;
    private AntiMonkeyButton addSubCategoryBtn;
    private LinearLayout subCategoriesLayout;
    private CircleImageView categoryImageView;
    private TextInputLayout categoryNameInputLayout;
    private EditText categoryNameTxt;
    private EditText categoryDescriptionTxt;

    private Uri imageUri;
    private Category category;

    private boolean isImageSet = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_category);
        setTitle(getString(R.string.category_title));

        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadSubCategoriesData(
                addSubCategoryBtn,
                subCategoryGridView,
                subCategoriesLayout
        );
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId() == R.id.subCategoryGridView) {
            menu.add(
                    Menu.NONE,
                    0,
                    0,
                    getString(R.string.category_delete)
            );
            menu.add(
                    Menu.NONE,
                    1,
                    0,
                    getString(R.string.analyze_expenses_title)
            );
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo adapterContextMenuInfo =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final SubCategory subCategory = (SubCategory) subCategoryGridView.getItemAtPosition(
                adapterContextMenuInfo.position
        );

        if (item.getItemId() == 0) {
            new SweetAlertDialog(
                    this,
                    SweetAlertDialog.WARNING_TYPE
            )
                    .setTitleText(getString(R.string.warning_title))
                    .setContentText(getString(R.string.category_sure_deletion))
                    .setConfirmText(getString(R.string.yes_btn))
                    .setCancelText(getString(R.string.no_btn))
                    .showCancelButton(true)
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            deleteSubCategory(subCategory);

                            sDialog.dismissWithAnimation();
                        }
                    })
                    .show();
        } else {
            AnalyzeExpenseDetailDialog dialog = AnalyzeExpenseDetailDialog.newInstance(
                    Constants.SUB_CATEGORY,
                    subCategory.getId().toString()
            );
            dialog.setCancelable(false);
            dialog.show(
                    getFragmentManager(),
                    Constants.ANALYZE_EXPENSE_DETAIL_DIALOG_TAG
            );
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.CAMERA_CAPTURE &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            if (resultCode == RESULT_OK) {
                ImageUtils.performCrop(
                        this,
                        imageUri,
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
                    categoryImageView.setImageBitmap(
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

            imageUri = data.getData();

            ImageUtils.performCrop(
                    this,
                    imageUri,
                    false
            );
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.takePhotoBtn:
                imageUri = ImageUtils.takePhoto(
                        this,
                        imageUri
                );

                break;

            case R.id.selectPhotoBtn:
                ImageUtils.selectPhoto(this);

                break;

            case R.id.removePhotoBtn:
                categoryImageView.setImageDrawable(
                        getResources().getDrawable(
                                R.drawable.ic_panorama_fish_eye_white_48dp
                        )
                );

                break;

            case R.id.addSubCategoryBtn:
                Intent intent = new Intent(
                        this,
                        SubCategoryActivity.class
                );
                intent.putExtra(
                        Constants.CATEGORY_ID_TAG,
                        category.getId().toString()
                );

                DisplayUtils.startNewActivity(
                        this,
                        intent,
                        false
                );

                break;

            case R.id.saveBtn:
                saveCategory();

                break;

            case R.id.cancelBtn:
                finish();

                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SubCategory selectedSubCategory =
                (SubCategory) view.getTag(R.layout.sub_category_item);

        Intent intent = new Intent(
                this,
                SubCategoryActivity.class
        );
        intent.putExtra(
                Constants.CATEGORY_ID_TAG,
                category.getId().toString()
        );
        if (selectedSubCategory != null) {
            intent.putExtra(
                    Constants.SUB_CATEGORY_ID_TAG,
                    selectedSubCategory.getId().toString()
            );
        }

        DisplayUtils.startNewActivity(
                this,
                intent,
                true
        );
    }

    @Override
    public void onMovedToScrapHeap(View view) {
        AsyncThumbnailImageLoaderTask categoryImageThread =
                (AsyncThumbnailImageLoaderTask) view.getTag(R.id.subCategoryPicture);

        if (categoryImageThread != null) {
            categoryImageThread.cancel(true);
        }
    }

    private void initViews() {
        categoryTitle =
                (TextView) findViewById(R.id.categoryTitle);
        categoryImageView =
                (CircleImageView) findViewById(R.id.categoryImage);
        categoryNameInputLayout =
                (TextInputLayout) findViewById(R.id.categoryNameTxtLayout);
        categoryNameTxt =
                (EditText) findViewById(R.id.categoryName);
        categoryDescriptionTxt =
                (EditText) findViewById(R.id.categoryDescription);
        AntiMonkeyButton takePhotoBtn =
                (AntiMonkeyButton) findViewById(R.id.takePhotoBtn);
        AntiMonkeyButton selectPhotoBtn =
                (AntiMonkeyButton) findViewById(R.id.selectPhotoBtn);
        AntiMonkeyButton removePhotoBtn =
                (AntiMonkeyButton) findViewById(R.id.removePhotoBtn);
        addSubCategoryBtn =
                (AntiMonkeyButton) findViewById(R.id.addSubCategoryBtn);
        AntiMonkeyButton saveBtn =
                (AntiMonkeyButton) findViewById(R.id.saveBtn);
        AntiMonkeyButton cancelBtn =
                (AntiMonkeyButton) findViewById(R.id.cancelBtn);
        subCategoriesLayout =
                (LinearLayout) findViewById(R.id.subCategoriesLayout);
        subCategoryGridView =
                (StaticAntiMonkeyGridView) findViewById(R.id.subCategoryGridView);
        registerForContextMenu(subCategoryGridView);

        String categoryId =
                getIntent().getStringExtra(Constants.CATEGORY_ID_TAG);
        if (categoryId != null) {
            category = Category.getCategoryById(
                    UUID.fromString(categoryId)
            );
        }

        loadData(categoryTitle);

        subCategoryGridView.setRecyclerListener(this);
        takePhotoBtn.setOnClickListener(this);
        selectPhotoBtn.setOnClickListener(this);
        removePhotoBtn.setOnClickListener(this);
        addSubCategoryBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    private void loadSubCategoriesData(
            AntiMonkeyButton addSubCategoryBtn,
            AntiMonkeyGridView subCategoryGridView,
            LinearLayout subCategoriesLayout
    ) {
        if (category != null && !isImageSet) {
            List<SubCategory> subCategories =
                    SubCategory.getAllSubCategoriesByParentId(category.getId());

            if (category.isDeletable()) {
                addSubCategoryBtn.setVisibility(View.VISIBLE);
            }

            if (!subCategories.isEmpty()) {
                subCategoriesLayout.setVisibility(View.VISIBLE);

                adapter = new SubCategoryAdapter(this);
                adapter.setSubCategoryList(subCategories);
                subCategoryGridView.setAdapter(adapter);
                subCategoryGridView.setOnItemClickListener(this);
            }
        }
    }

    private void loadData(TextView categoryTitle) {
        if (category != null) {
            categoryTitle.setText(category.getCategoryName());

            categoryImageView.setImageDrawable(
                    ImageUtils.blobToDrawable(
                            getResources(),
                            category.getCategoryPicture().getBlob()
                    )
            );
            categoryNameTxt.setText(category.getCategoryName());
            categoryDescriptionTxt.setText(category.getCategoryDescription());
        }
    }

    private void deleteSubCategory(SubCategory subCategory) {
        List<ExpenseTrackingDetails> expenseTrackingDetailsList =
                ExpenseTrackingDetails.getExpenseTrackingDetailBySubCategory(subCategory);

        if (!expenseTrackingDetailsList.isEmpty()) {
            for (ExpenseTrackingDetails details : expenseTrackingDetailsList) {
                details.delete();
            }

            subCategory.delete();
        }

        adapter.setSubCategoryList(SubCategory.getAllSubCategoriesByParentId(category.getId()));
        adapter.notifyDataSetChanged();
    }

    private boolean areFieldsValidated() {
        boolean areValidated = true;

        categoryNameInputLayout.setError(null);

        if (categoryNameTxt.getText().toString().trim().equals("")) {
            categoryNameInputLayout.setError(getString(R.string.empty_field_err));
            categoryNameInputLayout.requestFocus();
            areValidated = false;
        }

        Category category = Category.getCategoryByName(categoryNameTxt.getText().toString());
        if (this.category != null) {
            if (category != null && !this.category.getId().equals(category.getId())) {
                categoryNameInputLayout.setError(getString(R.string.category_duplication_err));
                categoryNameInputLayout.requestFocus();
                areValidated = false;
            }
        } else {
            if (category != null) {
                categoryNameInputLayout.setError(getString(R.string.category_duplication_err));
                categoryNameInputLayout.requestFocus();
                areValidated = false;
            }
        }

        return areValidated;
    }

    private boolean isCategoryEditable() {
        if (category != null && !category.isDeletable()) {
            new SweetAlertDialog(
                    this,
                    SweetAlertDialog.ERROR_TYPE
            )
                    .setTitleText(getString(R.string.error_title))
                    .setContentText(getString(R.string.category_not_editable))
                    .setConfirmText(getString(R.string.ok_btn))
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {

                            sDialog.dismissWithAnimation();
                        }
                    })
                    .show();

            return false;
        }

        return true;
    }

    private void saveCategory() {
        if (areFieldsValidated() && isCategoryEditable()) {
            if (category == null) {
                category = new Category();
            }

            category.setCategoryName(
                    categoryNameTxt.getText().toString().trim()
            );
            category.setCategoryDescription(
                    categoryDescriptionTxt.getText().toString().trim()
            );
            if (!isImageSet) {
                category.setCategoryPicture(
                        new Blob(
                                ImageUtils.defaultDrawableToBlob(
                                        categoryImageView.getDrawable()
                                )
                        )
                );
            } else {
                category.setCategoryPicture(
                        new Blob(
                                ImageUtils.drawableToBlob(
                                        categoryImageView.getDrawable()
                                )
                        )
                );
            }

            category.save();

            Toast.makeText(
                    this,
                    getString(R.string.category_saved_successfully),
                    Toast.LENGTH_SHORT
            ).show();

            finish();
        }
    }
}
