#!/bin/bash
## this deploy script works in Bash >= 3.2 

echo "Do you want to deploy?"
BRANCH=$(eval "git rev-parse --abbrev-ref HEAD")

read -r -p "Are you sure? [y/N] " response
if [[ "$response" =~ ^([yY][eE][sS]|[yY])$ ]]
then
    pendingCommits=$(eval "git log origin/master..master")
    if [ -z "$pendingCommits" ]
    then
      echo $(eval "curl https://app.bitrise.io/app/0521f01ccec6db76/build/start.json --data '{\"hook_info\":{\"type\":\"bitrise\",\"build_trigger_token\":\"fQaFMLvEVXwAbK60GbpPEQ\"},\"build_params\":{\"branch\":\"$BRANCH\",\"workflow_id\":\"deploy\"},\"triggered_by\":\"curl\"}'")
        echo ""
        echo ""
      echo "Thank you David, Regards Henry"
    else
      echo "\nsome pending commits were found!\n"
      echo $pendingCommits
    fi
else
    echo "YOU ARE A PUSSY!"
fi
echo ""
