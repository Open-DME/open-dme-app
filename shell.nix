{ pkgs ? import nixpkgs> {
  config.android_sdk.accept_license = true;
  config.allowUnfree = true;}, ... }:

let
jdk = pkgs.jdk17;
gradle= pkgs.gradle.override { java = jdk; };
pkgs = import nixpkgs {
        system = "x86_64-linux";
        config = {
          android_sdk.accept_license = true;
        };
};
in
pkgs.mkShell
{
  packages = with pkgs; [jdk gradle android-studio];
}

