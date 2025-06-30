document.addEventListener('DOMContentLoaded', function() {
    if (window.showEditProfileModalFlag) {
        var myModal = new bootstrap.Modal(document.getElementById('editProfileModal'));
        myModal.show();
    }

    const imageUploadInput = document.getElementById('imageUpload');
    if (imageUploadInput) {
        imageUploadInput.addEventListener('change', function() {
            this.form.submit();
        });
    }
});