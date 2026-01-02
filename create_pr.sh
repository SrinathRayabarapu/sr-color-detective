#!/bin/bash
# Script to push feature branch and create PR

echo "Step 1: Ensure remote repository exists on GitHub"
echo "If not, create it at: https://github.com/SrinathRayabarapu/sr-color-detective"
echo ""
echo "Step 2: Fetch latest from remote master (if it exists)"
git fetch origin master || echo "Remote master doesn't exist yet - this is expected for new repos"
echo ""
echo "Step 3: Merge remote master into feature branch (if remote exists)"
git merge origin/master || echo "No remote master to merge - skipping"
echo ""
echo "Step 4: Push feature branch"
git push -u origin feature/color-detective-app
echo ""
echo "Step 5: Create PR using one of these methods:"
echo ""
echo "Option A - Using GitHub CLI (if installed):"
echo "  gh pr create --base master --head feature/color-detective-app --title 'Color Detective Android App' --body-file PR_DESCRIPTION.md"
echo ""
echo "Option B - Using GitHub Web UI:"
echo "  Visit: https://github.com/SrinathRayabarapu/sr-color-detective/compare/master...feature/color-detective-app"
