/**
 * Returns the current live version metadata of the SkillBarter Android app.
 * Used by the in-app "Check for Updates" feature and auto-update prompts on launch.
 */
exports.getAppVersion = (req, res) => {
  res.json({
    versionCode: 2,
    versionName: '1.1',
    apkUrl: 'https://skillbarter-api-m0ev.onrender.com/app/download',
    notes: 'New update available! Improved session scheduling, bug fixes, and file attachment support.'
  });
};
